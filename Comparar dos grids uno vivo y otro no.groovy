/*********************************************/
/* leer un grid */
/* Autor: ??? */
/*********************************************/
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
import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.security.JiraAuthenticationContext
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.plugin.PluginAccessor
import com.opensymphony.workflow.InvalidInputException

//Constantes y managers
@Field cfm = ComponentAccessor.getCustomFieldManager()
@Field GridHelper gh = new GridHelper()
@Field CommonHelper ch = new CommonHelper()
@Field NfeedHelper nfh = new NfeedHelper()
@Field BDHelper bdh = new BDHelper()

def issueManager = ComponentAccessor.issueManager
def customFieldManager = ComponentAccessor.getCustomFieldManager();
PluginAccessor pluginAccessor = ComponentAccessor.getPluginAccessor();
JiraAuthenticationContext jiraAuthenticationContext = ComponentAccessor.getOSGiComponentInstanceOfType(JiraAuthenticationContext.class);
Object userObject = ch.getAdminUser() //jiraAuthenticationContext.getLoggedInUser();
User user = userObject instanceof ApplicationUser ? ((ApplicationUser) userObject).getDirectoryUser() : (User) userObject;
/************Acceso issue CONSOLA*******************/
//def issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("PPR-11491")
Class dataManagerClass = pluginAccessor.getClassLoader().findClass("com.idalko.jira.plugins.igrid.api.data.TGEGridTableDataManager");
def tgeGridDataManager = ComponentAccessor.getOSGiComponentInstanceOfType(dataManagerClass);
List<Map<String, Object>> gridDataListLC = new ArrayList<Map<String, Object>>();
StringBuilder result = new StringBuilder();
def tgeCustomFieldLC = customFieldManager.getCustomFieldObject("customfield_18221");
def tgeCustomFieldMod = customFieldManager.getCustomFieldObject("customfield_18214");
def controlOPE = []

def gridDataLC = tgeGridDataManager.readGridDataInEditMode(issue, tgeCustomFieldLC, null, null, 0, tgeGridDataManager.getRowCountInEditMode(issue, tgeCustomFieldLC,user), user);
//def gridDataMod = tgeGridDataManager.readGridDataInEditMode(issue, tgeCustomFieldMod, null, null, 0, tgeGridDataManager.getRowCountInEditMode(issue, tgeCustomFieldMod,user), user);
    
//IDs campos
//def gridIDLC = 18221L // ID tabla One time
def gridIDMod = 18214L
def arrayLCid = ['']
def arrayModid = ['']
def arrayLCpd = ['']
def arrayModpd = [''] 
def x = 0
def y = 0
boolean comprobar = true
//usuario de accion
//def user = ch.getAdminUser()

//MAIN
//def gridDataLC = gh.getAllGridRows(issue.id,gridIDLC,user)
def gridDataMod = gh.getAllGridRows(issue.id,gridIDMod,user)
def num = 0


try {
gridDataList = gridDataLC.getValues();
} catch (Exception e) {
log.error(e.toString())
}



for (int rowNumber = 0; rowNumber < gridDataList.size(); rowNumber++) {
Map<String, Object> row = gridDataList.get(rowNumber)
	x++
	log.error('LC-->'+row)
    arrayLCid[x] =row.IDpep 
    arrayLCpd[x]= row.pd.value
	log.warn(x+'LC-->'+arrayLCid[x]+' / PD-->'+arrayLCpd[x])
}

gridDataMod.each()//por cada Linea de Coste del Grid One Time
{
    y++
    arrayModid[y] =it.nompep 
    arrayModpd[y]= it.planned
	log.warn(y+'Mod-->'+ arrayModid[y]+' / PD-->'+arrayModpd[y])

}
log.error('Valor de x antes-->'+x)
num = x
x= 0
for(def w = 0; w < gridDataList.size()+1; w++ ){
    //for(def p= 0; p < gridDataLC.size()+1; p++ ){
	for(def p= 0; p < num+1; p++ ){
            //log.error('arrayLCid[w] ,' +arrayLCid[w] + 'arrayModid[x]'+arrayModid[p])
            if (arrayLCid[w] == arrayModid[p] && arrayModid[p] != null){
                log.error('Entra-->'+arrayLCid[w]+'='+arrayModid[p])
                log.warn('PD LC='+arrayLCpd[w] +', PD MOD = '+arrayModpd[p])
                comprobar = true
                if (arrayLCpd[w] == arrayModpd[p] ){
                    comprobar = true
                }else{
                    comprobar = false
					throw new InvalidInputException("No es posible modificar el tipo de PEP en los PEP aprobados en el proyecto.")

                }
            }
    }
}

return comprobar


