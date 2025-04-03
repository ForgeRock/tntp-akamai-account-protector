# Akamai Account Protector

The Akamai Account Protector node allows journey administrators to ingest Akamai Risk Signals into a Journey. Additional Akamai documentation includes:

* [Akamai Account Protector Documentation](https://techdocs.akamai.com/account-protector/docs/welcome-to-account-protector) 

## Compatibility

<table>
  <colgroup>
    <col>
    <col>
  </colgroup>
  <thead>
  <tr>
    <th>Product</th>
    <th>Compatible?</th>
  </tr>
  </thead>
  <tbody>
  <tr>
    <td><p>ForgeRock Identity Cloud</p></td>
    <td><p><span>Yes</span></p></td>
  </tr>
  <tr>
    <td><p>ForgeRock Access Management (self-managed)</p></td>
    <td><p><span>Yes</span></p></td>
  </tr>
  <tr>
    <td><p>ForgeRock Identity Platform (self-managed)</p></td>
    <td><p><span>Yes</span></p></td>
  </tr>
  </tbody>
</table>

## Inputs

The Akamai Account Protector node retrieves user risk data from the Akamai-User-Risk HTTP request header.

## Dependencies

You must set up the following before using the Akamai Account Protector node:

* [Akamai Account Protector](https://www.akamai.com/products/account-protector) 

## Configuration

<table>
  <thead>
  <th>Property</th>
  <th>Usage</th>
  </thead>

  <tr>
    <td>High Risk Threshold</td>
    <td>The maximum score for a High risk assessment. Scores above Medium and up to this value are categorized as High risk. Any score above this value are considered Critical risk.
    </td>
  </tr>

  <tr>
    <td>Medium Risk Threshold</td>
    <td>The maximum score for a medium risk assessment. Scores above Low and up to this value are categorized as Medium risk.
    </td>
  </tr>

  <tr>
    <td>Save Akamai Header to Shared State</td>
    <td>If checked, save the Akamai header in Shared State.
    </td>
  </tr>
</table>

## Outputs

The node writes the Akamai-User-Risk HTTP request header value to transient state.

## Outcomes

`Low Risk` Risk score equates to a low risk assessment.

`Medium Risk` Risk score equates to a medium risk assessment.

`High Risk` Risk score equates to a high risk assessment.

`No Score` Akamai-User-Risk header is empty.

`Error` There was an error during the verification process.

## Troubleshooting

If this node logs an error, review the log messages to find the reason for the error and address the issue appropriately.

## Example

The following example journey illustrates how to use the Akamai Account Protector node:

![](/docs/tree_node/akamai-account-protector-journey.png)

The Akamai Account Protector node parses through the Akamai-User-Risk HTTP request header and extracts the user risk score. Based on the risk score, the user is taken down the appropriate outcome.