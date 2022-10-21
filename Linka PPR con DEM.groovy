import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.ComponentManager
import groovy.xml.MarkupBuilder
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.config.properties.APKeys
import com.atlassian.jira.issue.link.LinkCollectionImpl;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.LinkCollectionImpl;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.link.IssueLink;
import java.util.Collection;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericEntityException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/************Acceso issue CONSOLA*******************/
def issue = ComponentAccessor.getIssueManager().getIssueObject("PPR-8384") //   child-->PPR-8359  Mother-->DEM-5293
//,issueLinkType=10600 es desde demanda-->PPR
//,issueLinkType=10300 es desde Business Case-->DEM
def x = 0
List<IssueLink> allOutIssueLink1 = ComponentAccessor.getIssueLinkManager().getOutwardLinks(issue.getId())
List<IssueLink> allOutIssueLink2 = ComponentAccessor.getIssueLinkManager().getInwardLinks(issue.getId())



def customFieldManager = ComponentAccessor.getCustomFieldManager()
def cfCategoria = customFieldManager.getCustomFieldObject('customfield_11500')
def categoria = issue.getCustomFieldValue(cfCategoria)
def summary = issue.getSummary()
log.debug('categoria-->'+categoria)
log.debug('summary-->'+summary)
def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)
def issueManager = ComponentAccessor.getIssueManager()
def user = ComponentAccessor.getUserManager().getUserByName("ajira")
log.warn('user-->'+user)
def resumen = issue.summary
log.warn('Summary-->'+issue.summary)
def query = jqlQueryParser.parseQuery("type = Demand and summary ~ '${resumen}'")
def search = searchProvider.search(query, user, PagerFilter.getUnlimitedFilter())
log.error("Key issues: ${search.total}")
def totales = search.total
def IssueMother = search.getIssues()
def mot =IssueMother[totales-1]
log.error('Mothers-->'+  mot )
def DEM = ''
def restult = search.getIssues().each { ticketBusqueda ->
    if(totales >= 1){
    def issueKeys = issueManager.getIssueObject(ticketBusqueda.id)
        if(issueKeys =="DEM-5426"){
            log.error('HOLA TU')
        }
    }else{
        log.error('solo hay una')
    }
}
for(x==0;x<totales;x++){
    DEM = restult[0].key
    DEMid = restult[0].id
            //log.error(restult[x].key)
        }
log.error('La que me interesa es-->'+DEM)
log.error('La que me interesa es-->'+DEMid)

if (categoria.toString() == 'Change request (Ampliación)' && (!allOutIssueLink2  ||  !allOutIssueLink1)){
log.error('hola mundillo')    
    		IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
            JiraAuthenticationContext authContext = ComponentAccessor.getJiraAuthenticationContext();
            //user = authContext.getLoggedInUser();
            issueLinkManager.createIssueLink(DEMid,issue.getId(), Long.parseLong('10300'),Long.valueOf(1), user)
            log.error('Enlace creado') 
    
}
return true

