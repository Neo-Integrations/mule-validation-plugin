<!DOCTYPE html>
<html>
  <head>
    <title> Validation Report </title>
        <style>
            .styled-table {
              border-collapse: collapse;
              margin: 25px 0;
              font-size: 0.9em;
              font-family: sans-serif;
              min-width: 400px;
              box-shadow: 0 0 20px rgba(0, 0, 0, 0.15);
            }
            .styled-table thead tr {
                background-color: #009879;
                color: #ffffff;
                text-align: left;
            }
            .styled-table th,
            .styled-table td {
                  padding: 12px 15px;
            }
            .styled-table tbody tr {
                border-bottom: 1px solid #dddddd;
            }

            .styled-table tbody tr:nth-of-type(even) {
                background-color: #f3f3f3;
            }

            .styled-table tbody tr:last-of-type {
                border-bottom: 2px solid #009879;
            }
            .styled-table tbody tr.active-row {
                font-weight: bold;
                color: #009879;
            }
        </style>
  </head>
<body>


  <h2>Static Code Analysis Stats</h2>
  <table class="styled-table">
    <tr>
      <th>Failed</th>
      <td>${failedSummary!}</td>
    </tr>
    <tr>
      <th>Passed</th>
      <td>${passedSummary!}</td>
    </tr>
    <tr>
      <th>Skipped</th>
      <td>${skippedSummary!}</td>
    </tr>
  </table>


  <#if failedRules?has_content>
        <h2>Static Code Analysis Summary</h2>
        <table class="styled-table">
          <tr>
            <th>Severity</th>
            <td>Rule Id</th>
            <th>Rule Description</th>
            <th>Overall Execution Status</th>
          </tr>
          <#list failedRules! as rule>
              <tr>
                <td>${rule.rule.severity}</td>
                <td>${rule.rule.ruleId}</td>
                <td>${rule.rule.description}</td>
                <td><#if rule.status=="FAILED"><span style="color:red">${rule.status}</span><#else>${rule.status}</#if></td>
              </tr>
          </#list>
        </table>
       </#if>

  <#if failedRules?has_content>
      <h2>Detailed Report</h2>
      <table class="styled-table">
        <tr>
          <th>Severity</th>
          <td>Rule Id</th>
          <th>Rule Description</th>
          <th>Overall Execution Status</th>
          <th>Processed File Name</th>
          <th>Node Name</th>
          <th>Node Attribute Value</th>
          <th>Each Node/File Processing Status</th>
          <th>Summary</th>
        </tr>
        <#list failedRules! as rule>
            <#if rule.results?has_content>
                <#list rule.results! as result>
                    <tr>
                      <td>${rule.rule.severity}</td>
                      <td>${rule.rule.ruleId}</td>
                      <td>${rule.rule.description}</td>
                      <td><#if rule.status=="FAILED"><span style="color:red">${rule.status}</span><#else>${rule.status}</#if></td>
                      <td>${result.fileName}</td>
                      <td>${result.nodeName!}</td>
                      <td>${result.attributeValue!}</td>
                      <td><#if result.status=="FAILED"><span style="color:red">${result.status}</span><#else>${result.status}</#if></td>
                      <td><#if result.status=="FAILED"><#if rule.rule.severity == "MAJOR" || rule.rule.severity == "CRITICAL">
                                <span style="color:red">ERROR: </span>
                           <#else>
                                <span style="color:blue">Warning: </span>
                           </#if></#if>
                           ${result.summary!}
                      </td>
                    </tr>
                 </#list>
             <#else>
                     <tr>
                       <td>${rule.rule.severity}</td>
                       <td>${rule.rule.ruleId}</td>
                       <td>${rule.rule.description}</td>
                       <td><#if rule.status=="FAILED"><span style="color:red">${rule.status}</span><#else>${rule.status}</#if></td>
                       <td></td>
                       <td></td>
                       <td></td>
                       <td></td>
                       <td></td>
                     </tr>
             </#if>
        </#list>
      </table>
     </#if>
</body>
</html>