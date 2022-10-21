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

/************Acceso issue CONSOLA*******************/
//def issue = ComponentAccessor.getIssueManager().getIssueObject("PPR-10839")
//IDs campos
def gridID = 18221 // ID tabla One time
//usuario de accion
def user = ch.getAdminUser()
//MAIN
def pluginAccessor = ComponentAccessor.getPluginAccessor();
changeSetClass = pluginAccessor.getClassLoader().findClass("com.idalko.jira.plugins.igrid.api.data.dto.TGEChangeSet");
def gridData = gh.getAllGridRows(issue.id,gridID,user)
def value1 = 0d
def value2 = 0d
def value3 = 0d
def value4 = 0d
def value5 = 0d
def suma = 0d
def existe = 0
def entrega; def unidadit; def grupoart; def subservicio; def proveedor; 
Class dataManagerClass = pluginAccessor.getClassLoader().findClass("com.idalko.jira.plugins.igrid.api.data.TGEGridTableDataManager");
def tgeGridDataManager = ComponentAccessor.getOSGiComponentInstanceOfType(dataManagerClass);
List changes = new ArrayList()
gridData.each()//por cada Linea de Coste del Grid One Time
{
log.warn(it)
    if(it.tipopep.name == 'SOF'){
        value1 = value1+(it.y1.multiply(0.125))
        value2 = value2+(it.y2.multiply(0.125))
        value3 = value3+(it.y3.multiply(0.125))
        value4 = value4+(it.y4.multiply(0.125))
        value5 = value5+(it.y5.multiply(0.125))
        entrega = it.actividad; unidadit = it.unidaditAux; grupoart = it.grupoartAux; subservicio = it.subservventa; proveedor = it.proveedorAux; 
        suma = suma+value1+value2+value3+value4+value5
    }else {
        log.error('No hay SOF con lo cual esta linea no la modifica')
    }
    suma = it.y1+it.y2+it.y3+it.y4+it.y5+suma
}
gridData.each()//por cada Linea de Coste del Grid One Time
{
    log.error('Suma-->'+suma)
    if(it.tipopep.name == 'ENP'){ //} && suma > 100000){
        existe = 1
        Map<String, Object> newRowValues = new HashMap<String, Object>();
        log.error("X  Row: ${it}")
        newRowValues.put("y1aux", value1 != null ? value1 : 0);
        newRowValues.put("y2aux", value2 != null ? value2 : 0);
        newRowValues.put("y3aux", value3 != null ? value3 : 0);
        newRowValues.put("y4aux", value4 != null ? value4 : 0);
        newRowValues.put("y5aux", value5 != null ? value5 : 0);
        log.error('Year 1 -->'+value1)
        def changeSet = changeSetClass.newInstance();
            changeSet.setRowId(it.id);
            changeSet.setValues(newRowValues);
            changes.add(changeSet);
            try {
                log.warn(' OK Grid ID tgeCustomFieldId-->'+gridID)
                tgeGridDataManager.applyChanges(issue.getId(), gridID, changes, user);
                log.error("OK Grid ID=" + gridID + " was successfully updated!\n");
            } catch (Exception e) {
                log.error("BAD Grid ID=" + gridID + " data cannot be updated: " + e.getMessage() + "\n");
            }
        log.error('EXISTE???? '+existe)
    }
}
gridData.each()//por cada Linea de Coste del Grid One Time
{
if(it.tipopep.name != 'ENP' && existe == 0 && suma > 100000){
          Map<String, Object> row = new HashMap<String, Object>();
            row.put("actividad", entrega != null ? entrega : '--')
            row.put("unidaditAux", unidadit != null ? unidadit : '--')
            row.put("grupoartAux", grupoart != null ? grupoart : '--')
            row.put("subservventa", subservicio != null ? subservicio : '--')
            row.put("proveedorAux", proveedor != null ? proveedor : '--')
            row.put("y1aux", value1 != null ? value1 : 0)
            row.put("y2aux", value2 != null ? value2 : 0)
            row.put("y3aux", value3 != null ? value3 : 0)
            row.put("y4aux", value4 != null ? value4 : 0)
            row.put("y5aux", value5 != null ? value5 : 0)
        log.error('Se crea una linea nueva de ENP')

    }
}
reindexar(issue)
def reindexar (def issue_r){
    try{
        def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
        boolean wasIndexing = ImportUtils.isIndexIssues();

        ImportUtils.setIndexIssues(true);
        issueIndexingService.reIndex(issue_r)
        ImportUtils.setIndexIssues(wasIndexing)

        return true
    }
    catch (Exception e){
        log.error("$issue_r: Error en reindexado $e");
        return false
    }
}
