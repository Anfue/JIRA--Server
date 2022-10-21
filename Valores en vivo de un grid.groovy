import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.JiraAuthenticationContext
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.plugin.PluginAccessor
import com.opensymphony.workflow.InvalidInputException
import groovy.transform.Field

def issueManager = ComponentAccessor.issueManager
def customFieldManager = ComponentAccessor.getCustomFieldManager();
PluginAccessor pluginAccessor = ComponentAccessor.getPluginAccessor();
JiraAuthenticationContext jiraAuthenticationContext = ComponentAccessor.getOSGiComponentInstanceOfType(JiraAuthenticationContext.class);
Object userObject = jiraAuthenticationContext.getLoggedInUser();
User user = userObject instanceof ApplicationUser ? ((ApplicationUser) userObject).getDirectoryUser() : (User) userObject;
// read grid data
Class dataManagerClass = pluginAccessor.getClassLoader().findClass("com.idalko.jira.plugins.igrid.api.data.TGEGridTableDataManager");
def tgeGridDataManager = ComponentAccessor.getOSGiComponentInstanceOfType(dataManagerClass);
List<Map<String, Object>> gridDataList = new ArrayList<Map<String, Object>>();
StringBuilder result = new StringBuilder();
def tgeCustomField = customFieldManager.getCustomFieldObject("customfield_18214");



try {
def gridData = tgeGridDataManager.readGridDataInEditMode(issue, tgeCustomField, null, null, 0, tgeGridDataManager.getRowCountInEditMode(issue, tgeCustomField,user), user);
gridDataList = gridData.getValues();
} catch (Exception e) {
log.error(e.toString())
}



for (int rowNumber = 0; rowNumber < gridDataList.size(); rowNumber++) {
Map<String, Object> rowData = gridDataList.get(rowNumber)
log.warn('Toda la tabla-->'+rowData)
 log.error('it.totalSolicitado-->'+rowData.totalSolicitado.toString()+' y tipoPEP = '+rowData.tipopep.value)
    if(rowData.tipopep.value == 'ENP' && rowData.totalSolicitado != 0){
        log.error('Entra ENP')
        throw new InvalidInputException("Los valores de ENP no se pueden modificar( ${+rowData.totalSolicitado} )")
        return false
    }else {
        log.error('Entra No ENP')
        return true
    }
}


/*********************************************/
/* leer un grid */
/* Autor: Jfernafu */
/*********************************************//*
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import groovy.transform.Field
import com.atlassian.jira.ComponentManager;
import Helper.CommonHelper
import Helper.BDHelper
import Helper.GridHelper
import Helper.NfeedHelper
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.opensymphony.workflow.InvalidInputException


//import com.atlassian.plugin.PluginAccessor;

//Constantes y managers
@Field cfm = ComponentAccessor.getCustomFieldManager()
@Field GridHelper gh = new GridHelper()
@Field CommonHelper ch = new CommonHelper()
@Field NfeedHelper nfh = new NfeedHelper()
@Field BDHelper bdh = new BDHelper()

def CAPEX = [10900,12902,12903,14350] //id de categorias que son consideradas CAPEX
def totales = [0,0,0,0,0] //total por año en el PPR
def actividades = [] //agrupado de las LC por actividad
def actividadesSinLinea = [] //listado de Keys de actividades que no tienen ninguna LC asociada

/************Acceso issue CONSOLA*******************/
//def issue = ComponentAccessor.getIssueManager().getIssueObject("AJ-313")

//IDs campos
/*
def gridID = 18214 // ID tabla

//usuario de accion
def user = ch.getAdminUser()

//MAIN
def gridData = gh.getAllGridRows(issue.id,gridID,user)

gridData.each()//por cada Linea de Coste del Grid One Time
{
    log.warn('Toda la tabla-->'+it)
 log.error('it.totalSolicitado-->'+it.totalSolicitado.toString()+' y tipoPEP = '+it.tipopep.value)
    if(it.tipopep.value == 'ENP' && it.totalSolicitado != 0){
        log.error('Entra ENP')
        throw new InvalidInputException("Los valores de ENP no se pueden modificar( ${+it.totalSolicitado} )")
        return false
    }else {
        log.error('Entra No ENP')
        return true
    }
}

*/

