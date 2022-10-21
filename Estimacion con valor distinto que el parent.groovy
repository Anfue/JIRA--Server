import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.component.ComponentAccessor
import java.util.Collection;
import org.apache.log4j.Logger
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink;


// si la tarifa de la estimacion tiene valor 0 facturable = null sino el valor es el del parent siempre desde issue = subtask enm la transicion editar

MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject("HDEV-192031")  // estimacion 192022 projecto 188072
List<IssueLink> allOutIssueLink2 = ComponentAccessor.getIssueLinkManager().getInwardLinks(issue.getId())
def cfFacturable = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(10501L)
def val_facturableE = issue.getCustomFieldValue(cfFacturable)
def cfTarifa = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(13005L)
def val_TarifaE = issue.getCustomFieldValue(cfTarifa)
def val_facturableP;def val_TarifaP;def tipo

for (final IssueLink link : allOutIssueLink2){
    	final String name = link.getIssueLinkType().getName();
   	   	MutableIssue story = (MutableIssue) link.getSourceObject();
    	tipo = story.issueType.name
    	val_facturableP = story.getCustomFieldValue(cfFacturable)
    	val_TarifaP = story.getCustomFieldValue(cfTarifa)
            if( tipo.toString() == 'Story' && val_TarifaE != val_TarifaP){ // lo suyo sería poner los id de type
                log.info ("Story: "+val_TarifaP)
                cfFacturable.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cfFacturable), val_facturableP),new DefaultIssueChangeHolder());
            } else{
                log.info 'Los valore de la story padre y su estimacion son iguales'
            }
        
}



