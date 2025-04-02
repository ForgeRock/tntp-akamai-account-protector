/*
 * This code is to be used exclusively in connection with Ping Identity Corporation software or services.
 * Ping Identity Corporation only offers such software or services to legal entities who have entered into
 * a binding license agreement with Ping Identity Corporation.
 *
 * Copyright 2024 Ping Identity Corporation. All Rights Reserved
 */

package org.forgerock.am.marketplace.akamaiaccountprotector;

import static org.forgerock.am.marketplace.akamaiaccountprotector.AkamaiAccountProtectorNode.AkamaiOutcomeProvider.*;
import static org.forgerock.json.JsonValue.json;
import static org.forgerock.json.JsonValue.object;

import java.util.*;
import javax.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.forgerock.openam.annotations.sm.Attribute;
import org.forgerock.openam.auth.node.api.NodeState;
import org.forgerock.util.i18n.PreferredLocales;
import org.forgerock.openam.auth.node.api.*;
import org.forgerock.json.JsonValue;
import org.forgerock.openam.auth.node.api.OutcomeProvider;

import org.apache.commons.lang.exception.ExceptionUtils;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 * The Akamai Account Protector node allows journey administrators to ingest Akamai Risk Signals into a Journey.
 */
@Node.Metadata(outcomeProvider = AkamaiAccountProtectorNode.AkamaiOutcomeProvider.class,
        configClass = AkamaiAccountProtectorNode.Config.class,
        tags = {"marketplace", "trustnetwork"})
public class AkamaiAccountProtectorNode implements Node {

    private static final Logger logger = LoggerFactory.getLogger(AkamaiAccountProtectorNode.class);
    private static final String LOGGER_PREFIX = "[AkamaiAccountProtector]" + AkamaiAccountProtectorPlugin.LOG_APPENDER;

    private static final String BUNDLE = AkamaiAccountProtectorNode.class.getName();

    private final Config config;

    /**
     * Configuration for the Akamai Account Protector node.
     */
    public interface Config {
        /**
         * Shared state attribute containing High Limit Value
         *
         * @return The High Limit Value shared state attribute
         */
        @Attribute(order = 100, requiredValue = true)
        default Integer highValue() {
            return 50;
        }

        /**
         * Shared state attribute containing Medium Limit Value
         *
         * @return The Medium Limit Value shared state attribute
         */
        @Attribute(order = 200, requiredValue = true)
        default Integer mediumValue() {
            return 25;
        }

        /**
         * Shared state attribute to enable saving Akamai header.
         *
         * @return True if the Akamai header should be saved to shared state; false otherwise.
         */
        @Attribute(order = 300)
        default boolean saveAkamaiHeader() {
            return false;
        }
    }

    /**
     * The Akamai Account Protector node constructor.
     *
     * @param config the node configuration.
     */
    @Inject
    public AkamaiAccountProtectorNode(@Assisted Config config) {
        this.config = config;
    }

    @Override
    public Action process(TreeContext context) {

        // Create the flow input based on the node state
        NodeState nodeState = context.getStateFor(this);

        try {
            // Capture the header value
            List<String> akamaiUserRiskHeader = context.request.headers.get("Akamai-User-Risk");

            if(akamaiUserRiskHeader != null && !akamaiUserRiskHeader.isEmpty()) {

                // Split header at the semicolon delimiters
                String[] akamaiHeader = akamaiUserRiskHeader.get(0).split(";");

                // Loop through and add each key-value pair individually to transient state
                for (String keyValuePair : akamaiHeader) {
                    String[] pairs = keyValuePair.split("=", 2);
                    if (pairs.length == 2) {
                        nodeState.putTransient(pairs[0], pairs[1]);
                    }
                }

                // If true, save the Akamai HTTP header to shared state
                if (config.saveAkamaiHeader()) {
                    for (String keyValuePair : akamaiHeader) {
                        String[] pairs = keyValuePair.split("=", 2);
                        if (pairs.length == 2) {
                            nodeState.putShared(pairs[0], pairs[1]);
                        }
                    }
                }

                // Parse risk signals object to retrieve the score key-value pair
                JsonValue riskSignalsObject = json(object(1));
                for (String keyValuePair : akamaiHeader) {
                    String[] pairs = keyValuePair.split("=", 2);
                    if (pairs.length == 2) {
                        riskSignalsObject.put(pairs[0], pairs[1]);
                    }
                }

                // Retrieve the 'score' value from the Akamai request header
                Integer overallScore = riskSignalsObject.get("score").asInteger();

                // Handle the outcomes based on overallValue
                if (overallScore <= config.mediumValue()) {
                    return Action.goTo(LOW_RISK_OUTCOME_ID).build();
                } else if (overallScore <= config.highValue()) {
                    return Action.goTo(MEDIUM_RISK_OUTCOME_ID).build();
                } else {
                    return Action.goTo(HIGH_RISK_OUTCOME_ID).build();
                }
            } else {
                return Action.goTo(NO_SCORE_OUTCOME_ID).build();
            }
        } catch (Exception ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            logger.error(LOGGER_PREFIX + "Exception occurred: ", ex);
            context.getStateFor(this).putTransient(LOGGER_PREFIX + "Exception", new Date() + ": " + ex.getMessage());
            context.getStateFor(this).putTransient(LOGGER_PREFIX + "StackTrace", new Date() + ": " + stackTrace);
            return Action.goTo(CLIENT_ERROR_OUTCOME_ID).build();
        }
    }

    @Override
    public InputState[] getInputs() {
        return new InputState[]{
                new InputState("referer", false),
        };
    }

    @Override
    public OutputState[] getOutputs() {
        return new OutputState[]{};
    }

    public static class AkamaiOutcomeProvider implements OutcomeProvider {

        static final String LOW_RISK_OUTCOME_ID = "low";
        static final String MEDIUM_RISK_OUTCOME_ID = "medium";
        static final String HIGH_RISK_OUTCOME_ID = "high";
        static final String NO_SCORE_OUTCOME_ID = "noscore";
        static final String CLIENT_ERROR_OUTCOME_ID = "clientError";

        @Override
        public List<Outcome> getOutcomes(PreferredLocales locales, JsonValue jsonValue) {
            ResourceBundle bundle = locales.getBundleInPreferredLocale(BUNDLE, AkamaiOutcomeProvider.class.getClassLoader());

            ArrayList<Outcome> outcomes = new ArrayList<>();

            outcomes.add(new Outcome(LOW_RISK_OUTCOME_ID, bundle.getString(LOW_RISK_OUTCOME_ID)));
            outcomes.add(new Outcome(MEDIUM_RISK_OUTCOME_ID, bundle.getString(MEDIUM_RISK_OUTCOME_ID)));
            outcomes.add(new Outcome(HIGH_RISK_OUTCOME_ID, bundle.getString(HIGH_RISK_OUTCOME_ID)));
            outcomes.add(new Outcome(NO_SCORE_OUTCOME_ID, bundle.getString(NO_SCORE_OUTCOME_ID)));
            outcomes.add(new Outcome(CLIENT_ERROR_OUTCOME_ID, bundle.getString(CLIENT_ERROR_OUTCOME_ID)));

            return outcomes;
        }
    }
}
