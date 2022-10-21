/*********************************************/
/* leer un grid */
/* Autor: Jfernafu */
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
def issue = ComponentAccessor.getIssueManager().getIssueObject("PPR-2892")





//IDs campos DES
def gridID = 18221L // Verificacion LC finance
def gridIDPEP = 18120L // ID tabla PEP
def gridIDMod = 18214L // Modificacion PEP existente
//IDs campos PRO
//def gridID = 16224L // Verificacion LC finance
//def gridIDPEP = 16221L // ID tabla PEP
//def gridIDMod = 16230L // Modificacion PEP existente





//usuario de accion
def user = ch.getAdminUser()






//MAIN



def gridData = gh.getAllGridRows(issue.id, gridID, user)
def gridDataPEP = gh.getAllGridRows(issue.id, gridIDPEP, user)
def gridDataMod = gh.getAllGridRows(issue.id, gridIDMod, user)




gridData.each()//por cada Linea de Coste del Grid One Time
{
log.warn(it)

}