# Akamai Account Protector

The Akamai Account Protector node allows journey administrators to ingest Akamai Risk Signals into a Journey.

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

The Akamai Account Protector node retrieves User Risk data from the Akamai-User-Risk HTTP request header.

## Configuration

<table>
  <thead>
  <th>Property</th>
  <th>Usage</th>
  </thead>

  <tr>
    <td>Low Risk Threshold</td>
      <td>The maximum score for a low risk assessment. Scores up to this value are categorized as Low risk.
      </td>
  </tr>

  <tr>
    <td>Medium Risk Threshold</td>
    <td>The maximum score for a medium risk assessment. Scores above Low and up to this value are categorized as Medium risk.
    </td>
  </tr>

  <tr>
    <td>High Risk Threshold</td>
    <td>The maximum score for a High risk assessment. Scores above Medium and up to this value are categorized as High risk. Any score above this value are considered Critical risk.
    </td>
  </tr>

  <tr>
    <td>Save Akamai Header to Shared State</td>
    <td>If checked, saves the Akamai header in Shared State
    </td>
  </tr>
</table>

## Outputs

`Akamai User Risk Signals`

## Outcomes

`Low Risk` Risk score equates to a low risk assessment.

`Medium Risk` Risk score equates to a medium risk assessment.

`High Risk` Risk score equates to a high risk assessment.

`Critical Risk` Risk score equates to a critical risk assessment.

`Error` There was an error during the verification process.

## Troubleshooting

If this node logs an error, review the log messages to find the reason for the error and address the issue appropriately.
