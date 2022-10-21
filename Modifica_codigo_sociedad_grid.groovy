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

//Constantes y managers
@Field cfm = ComponentAccessor.getCustomFieldManager()
@Field GridHelper gh = new GridHelper()
@Field CommonHelper ch = new CommonHelper()
@Field NfeedHelper nfh = new NfeedHelper()
@Field BDHelper bdh = new BDHelper()
customFieldManager = ComponentAccessor.customFieldManager
issueManager = ComponentAccessor.issueManager
def CAPEX = [10900,12902,12903,14350] //id de categorias que son consideradas CAPEX
def totales = [0,0,0,0,0] //total por año en el PPR
def actividades = [] //agrupado de las LC por actividad
def actividadesSinLinea = [] //listado de Keys de actividades que no tienen ninguna LC asociada

/************Acceso issue CONSOLA*******************/
def issue = ComponentAccessor.getIssueManager().getIssueObject("PPR-33045")

//IDs campos
def gridID = 16224L // Verificacion LC finance
def gridIDPEP = 16221L // ID tabla PEP
def gridIDMod = 16230L // Modificacion PEP existente
def gridIDNIT = 16233L // Costes NO IT
def cf_SociedadPagadora = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(15701L)
def ExcepcionENP_id = 15704L
def value_activity
def issueKeys
def codigosociedad_val
def val_SociedadPagadora
//usuario de accion
def user = ch.getAdminUser()

//MAIN

def gridData = gh.getAllGridRows(issue.id, gridID, user)
def gridDataPEP = gh.getAllGridRows(issue.id, gridIDPEP, user)
def gridDataMod = gh.getAllGridRows(issue.id, gridIDMod, user)
def gridDataNIT = gh.getAllGridRows(issue.id, gridIDNIT, user)

gridDataPEP.each()//por cada Linea de Coste del Grid One Time
{ 
log.error(it)
     if ( it.tipopep.name == 'ENP'){
    value_activity = Long.valueOf(it.nivelsup.toString())
    issueKeys = issueManager.getIssueObject(value_activity)
    val_SociedadPagadora = issueKeys.getCustomFieldValue(cf_SociedadPagadora)[10]
    if (val_SociedadPagadora == '1'){
        codigosociedad_val = '08'
    }
    if (val_SociedadPagadora == '2'){
        codigosociedad_val = 9502
    }
    log.error('Key->'+val_SociedadPagadora+', Tipo->'+it.tipopepaux+', codigo sociedad original->'+it.codigosociedad+', codigo impuesto->'+codigosociedad_val)
    Map < String, Object > row = new HashMap < String, Object > ();
    //log.warn(' it.y1-->'+ it.y1);log.error('value1-->'+value1);log.error('VENPOr1-->'+VENPOr1)
    //log.error('En PEP de SOF('+Id+') tiene como ENP el '+it.IDpep +' y se pone el valor año 1 de '+value1)
    row.put("codigosociedad", codigosociedad_val)
    log.error('Se modifica el ENP existente en tabla PEP')
    gh.updateRowGrid(issue.getId(), gridIDPEP, user, row, it.id)
    }   else{
        log.error('NO HAY ENP QUE TRATAR')
    } 

}