import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter

def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchService = ComponentAccessor.getComponent(SearchService)
def issueManager = ComponentAccessor.getIssueManager()
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

// edit this query to suit
def query = jqlQueryParser.parseQuery("project = JRA and assignee = currentUser()")

def search = searchService.search(user, query, PagerFilter.getUnlimitedFilter())

log.debug("Total issues: ${search.total}")

search.getIssues().each { ticketBusqueda ->
    def issueKeys = issueManager.getIssueObject(ticketBusqueda.id)
    log.error(issueKeys)
}