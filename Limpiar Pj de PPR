import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.web.bean.PagerFilter
import com.opensymphony.workflow.InvalidInputException
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.customfields.option.Option

def customFieldManager = ComponentAccessor.customFieldManager 
def issueManager = ComponentAccessor.issueManager
def issue = ComponentAccessor.getIssueManager().getIssueObject("PPR-11481")
def Mensaje = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(14503L); //obtenemos el campo mensaje change request
def NuevoMensaje = null //le damos valor nulo
def seleccionProyecto = customFieldManager.getCustomFieldObject(11507L) //Obtenemos el campo proyecto
def valueProject = issue.getCustomFieldValue(seleccionProyecto) //obtenemos el número proyecto PJ-547
def categoria = customFieldManager.getCustomFieldObject(11500L) //obtenemos el campo categoria
def categoriaValue = issue.getCustomFieldValue(categoria)//.getOptionId() //Obtenemos el valor ID del campo categora
log.error ('categoriaValue-->'+Mensaje)
if(valueProject == null){
    return
}
def ValueProjecto = valueProject[10,11,12,13,14].toString()
log.error(valueProject + '-->'+valueProject[10,11,12,13,14])
proyecto = issueManager.getIssueObject(Long.valueOf(nfeed_Limpiar_Valor(valueProject[10,11,12,13,14])))
//si el valor de categoria es igual 10903 Change request (Ampliación) borramos el mensaje
if (categoriaValue == 10903){
		Mensaje.updateValue(null, proyecto, new ModifiedValue(Mensaje.getValue(issue),NuevoMensaje), new DefaultIssueChangeHolder())
}


def nfeed_Limpiar_Valor (String nfeedText){
    def auxText
    auxText = nfeedText.replace("<content>","")
    auxText = auxText.replace("  <value>","")
    auxText = auxText.replace("</content>","")
    auxText = auxText.replace("</value>","")
    auxText = auxText.replace("\n","")
    return auxText
}