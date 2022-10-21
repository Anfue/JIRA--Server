import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder

// remove jira links
issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("DEM-8867") // PPR-  36841
userAdmin = ComponentAccessor.getUserManager().getUserByName("ajira")
def subTasksList = issue.getSubTaskObjects()

def outwardLinksDem = ComponentAccessor.getIssueLinkManager().getOutwardLinks(issue.id)
log.error outwardLinksDem
//createIssueLink(Long sourceIssueId, Long destinationIssueId, Long issueLinkTypeId, Long sequence, ApplicationUser remoteUser)

ComponentAccessor.getIssueLinkManager().createIssueLink(128641L, 129643L, 10100L, 1, userAdmin) 

//id=153332,sourceId=148133,destinationId=148134,issueLinkType=10300]] 
    log.error(subTasksList)
    
    
    
    
    
    
    
    
    
    