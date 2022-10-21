import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.IssueManager

//def issue = ComponentAccessor.issueManager.getIssueObject("HDEV-106014");
def valor = issue.getOriginalEstimate()
//issue.setOriginalEstimate(21600)
issue.setEstimate(0)
issue.setTimeSpent(valor)
issue.store()

