import com.atlassian.jira.config.util.JiraHome
 
 
String rutaLogs = "/app/atlassian/jira/atlassian-jira/WEB-INF/classes/";
String nombreScript = "jira-database-values-plugin-12202.properties"
String comando = "cat /app/atlassian/jira/atlassian-jira/WEB-INF/classes/jira-database-values-plugin-12202.properties";
 
def process = comando.execute()
def output= process.in.text;
 
return output.replace("\n","<br/>");
