import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import groovy.xml.MarkupBuilder
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.config.properties.APKeys
import com.atlassian.jira.issue.link.LinkCollectionImpl;
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
import com.atlassian.jira.issue.link.IssueLink;
import java.util.Collection;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.ofbiz.core.entity.GenericEntityException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.UpdateIssueRequest
import com.atlassian.jira.issue.link.IssueLinkManager;

/************Acceso issue CONSOLA*******************/
//def issue = ComponentAccessor.getIssueManager().getIssueObject("HDEV-106797") //   child-->PPR-8359  Mother-->DEM-5293
//,issueLinkType=10600 es desde demanda-->PPR
//,issueLinkType=10300 es desde Business Case-->DEM

MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject('HDEV-106797')
log.error('Id issue->'+issue.id)
IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
IssueManager issueManager = ComponentAccessor.getIssueManager();
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();

List<IssueLink> allOutIssueLink1 = ComponentAccessor.getIssueLinkManager().getOutwardLinks(issue.getId())
List<IssueLink> allOutIssueLink2 = ComponentAccessor.getIssueLinkManager().getInwardLinks(issue.getId())
for (final IssueLink link : allOutIssueLink2){
    	final String name = link.getIssueLinkType().getName();
   	   	MutableIssue historia = (MutableIssue) link.getSourceObject();
        log.error ("Historia: "+historia)
}
for (final IssueLink link : allOutIssueLink1){
    	final String name = link.getIssueLinkType().getName();
   	   	MutableIssue epica = (MutableIssue) link.getSourceObject();
        log.error ("epica: "+epica)
}