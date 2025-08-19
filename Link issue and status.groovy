
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.link.IssueLinkManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder

// Get the current issue (for testing in script console)
def issue = Issues.getByKey('HDEV-420470')  // <- solo para pruebas
log.error('Id issue->'+issue.id)

IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager()
IssueManager issueManager = ComponentAccessor.getIssueManager()
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()

// Get all outward links (this issue points to other issues)
List<IssueLink> allOutIssueLink1 = ComponentAccessor.getIssueLinkManager().getOutwardLinks(issue.getId())
// Get all inward links (other issues point to this issue)
List<IssueLink> allOutIssueLink2 = ComponentAccessor.getIssueLinkManager().getInwardLinks(issue.getId())

// Process inward links
for (final IssueLink link : allOutIssueLink2) {
    final String linkType = link.getIssueLinkType().getName()
    MutableIssue sourceIssue = (MutableIssue) link.getSourceObject()
    log.error("INWARD - Type: ${linkType}, Linked Issue: ${sourceIssue.key}")

}

// Process outward links
for (final IssueLink link : allOutIssueLink1) {
    final String linkType = link.getIssueLinkType().getName()
    MutableIssue linkedIssue = (MutableIssue) link.getDestinationObject()
    log.error("OUTWARD - Type: ${linkType}, Linked Issue: ${linkedIssue.key}")
    if(linkType == "jira_subtask_link") {
        if(linkedIssue.getIssueTypeId() == '10105'){
            log.warn('entra y la issue es '+linkedIssue.getIssueTypeId())   
            def subtask = Issues.getByKey(linkedIssue.key)
                log.warn ('Tipos->'+subtask.statusId)
        }
    
        

    }
}
