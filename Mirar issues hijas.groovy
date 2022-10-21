import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.JiraAuthenticationContext
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.LinkCollectionImpl
import java.util.Collection;
import java.util.Iterator;



Collection subTasks = issue.getSubTaskObjects()
ApplicationUser currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();



for(subTask in subTasks) {
if(currentUser == issue.getAssignee() && issue.issueType.name == "Validation"){
return true
}
}
return false