/* Following script retrieves the akamai-user-risk custom http header and returns an outcome
   of High, Medium, or Low depending on the score levels from Akamai Account Protector.

   If the akamai-user-risk custom http header is not available, "No Score" outcome is returned and
   if the script encounters an error the "Error" outcome is returned.

   Adjust the numerical values of High Risk and Medium Risk below in the two constants HIGH_SCORE and MEDIUM_SCORE.

 */

// Adjust as require
var HIGH_SCORE = 50;
var MEDIUM_SCORE = 25;

try {
    var riskHeader = requestHeaders.get("akamai-user-risk");

    if(riskHeader != null) {
        var riskString = riskHeader.get(0);


        var riskArray = riskString.split(';')
        var scoreArray = riskArray[3].split('=');
        var score = Number(scoreArray[1]);

        nodeState.putShared("score", score);

        if(score > HIGH_SCORE) {
            outcome = "High";
        } else if(score > MEDIUM_SCORE) {
            outcome = "Medium";
        } else {
            outcome = "Low";
        }
    } else {
        outcome = "No Score"
    }
} catch(e) {
    outcome = "Error";
}
