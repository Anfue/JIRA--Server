/*********************************************/
/* leer un grid */
/* Autor: EDIEZ */
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
import com.atlassian.jira.issue.index.IssueIndexingService

//Constantes y managers
@Field cfm = ComponentAccessor.getCustomFieldManager()
@Field GridHelper gh = new GridHelper()
@Field CommonHelper ch = new CommonHelper()
@Field NfeedHelper nfh = new NfeedHelper()
@Field BDHelper bdh = new BDHelper()

/************Acceso issue CONSOLA*******************/
def issue = ComponentAccessor.getIssueManager().getIssueObject("AJ-673")
//IDs campos
def gridID = 18221L // ID tabla One time
def gridIDPEP = 18120L // ID tabla PEP
def gridIDMod = 18214L // Modificacion PEP existente
//usuario de accion
def user = ch.getAdminUser()
log.error('USER-->' + user.class)
//MAIN
def pluginAccessor = ComponentAccessor.getPluginAccessor();
changeSetClass = pluginAccessor.getClassLoader().findClass("com.idalko.jira.plugins.igrid.api.data.dto.TGEChangeSet");
def gridData = gh.getAllGridRows(issue.id, gridID, user)
def gridDataPEP = gh.getAllGridRows(issue.id, gridIDPEP, user)
def gridDataMod = gh.getAllGridRows(issue.id, gridIDMod, user)
def value1 = 0d
def value2 = 0d
def value3 = 0d
def value4 = 0d
def value5 = 0d
def suma = 0d
def conSOF = 0
def conENP = 0
def xxx = 0
def xxxx = 0
def idsENP
def sol1 = 0d;
def sol2 = 0d;
def sol3 = 0d;
def sol4 = 0d;
def sol5 = 0d;
def IDENP = [0]
boolean existe = false
def entrega;
def unidadit;
def grupoart;
def subservicio;
def proveedor;
Class dataManagerClass = pluginAccessor.getClassLoader().findClass("com.idalko.jira.plugins.igrid.api.data.TGEGridTableDataManager");
def tgeGridDataManager = ComponentAccessor.getOSGiComponentInstanceOfType(dataManagerClass);
List changes = new ArrayList()
def rows = tgeGridDataManager.readGridData(issue.getId(), gridID, null, null, 0, 100, user);
def values = rows.getValues()
def cfCategoria = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(11500L)
def categoriaValue = issue.getCustomFieldValue(cfCategoria).optionId
log.error('categoriaValue-->' + categoriaValue)


gridData.each() //por cada Linea de Verificacion LC finance
{
    log.error('ppp->' + it)
    if (it.tipopep.name == 'ENP') {
        xxx++
        log.warn('IDENP antes-->' + IDENP)
        IDENP = it.IDpep
        log.warn('IDENP Despues-->' + IDENP)
        sol1 = it.soly1;
        sol2 = it.soly2;
        sol3 = it.soly3;
        sol4 = it.soly4;
        sol5 = it.soly5;
        Set < Long > rowIdsToDelete = new HashSet < Long > ();
        log.error('lastRow-->' + it.id)
        rowIdsToDelete.add(it.id);
        tgeGridDataManager.deleteRows(issue.id, gridID, rowIdsToDelete, user);
    }
    suma = suma + it.y1 + it.y2 + it.y3 + it.y4 + it.y5

}
gridDataPEP.each() //por cada Linea de Costede PEP
{
    log.error('ppp 2->' + it)
    if (it.tipopep.name == 'ENP') {
        xxxx++
        IDENP = it.IDpep
        Set < Long > rowIdsToDelete = new HashSet < Long > ();
        log.error('lastRow-->' + it.id)
        rowIdsToDelete.add(it.id);
        tgeGridDataManager.deleteRows(issue.id, gridIDPEP, rowIdsToDelete, user);
    }
}
def cont = 0
def ENPs = [0]
gridDataMod.each() //por cada Linea de Modificacion PEP existentes
{
    //log.warn(it)
    if (it.tipopep == 'ENP') {
        cont++
        idsENP = it.nompep
        ENPs[cont] = it.nompep
        log.error('Nompep-->' + it.nompep)
        log.error('ENPs['+cont+']-->'+ENPs[cont])
    }
}
def eventoENP = 'A'
def event = 'A'
def p = 0
def idsENPs
def valueS1=0d;def valueS2=0d;def valueS3=0d;def valueS4=0d;def valueS5=0d;
gridDataPEP.each() 
{
    log.error('Hola tu')
    log.error('XXX-->' + xxx + ', xxxx-->' + xxxx)
    def xx = 0

    if (it.tipopep.name == 'SOF' && (xxx > 0 || xxxx > 0)) {
        xx++
        eventoENP = 'M'
		log.error('Doble xx-->')
    }
    if (eventoENP == 'M') {
        //idsENP = (IDENP == null ? '' :IDENP[xx])
    }
    value1 = 0;
    value2 = 0;
    value3 = 0;
    value4 = 0;
    value5 = 0;
    log.warn(it)
    log.error('Suma=' + suma + ' y tipo de pep= ' + it.tipopep.name)
    if (it.tipopep.name == 'SOF' && suma > 100000) {
        log.warn('IN-->' + it)
        conSOF++
        value1 = value1 + (it.y1.multiply(0.125))
        value2 = value2 + (it.y2.multiply(0.125))
        value3 = value3 + (it.y3.multiply(0.125))
        value4 = value4 + (it.y4.multiply(0.125))
        value5 = value5 + (it.y5.multiply(0.125))
        valueS1 = (sol1 == null ? valueS1 + (it.soly1.multiply(0.125)) : sol1)
        valueS2 = (sol1 == null ? valueS2 + (it.soly2.multiply(0.125)) : sol2)
        valueS3 = (sol1 == null ? valueS3 + (it.soly3.multiply(0.125)) : sol3)
        valueS4 = (sol1 == null ? valueS4 + (it.soly4.multiply(0.125)) : sol4)
        valueS5 = (sol1 == null ? valueS5 + (it.soly5.multiply(0.125)) : sol5)
        entrega = it.nombrepepaux;
        unidadit = it.unidadit;
        grupoart = it.grupoartAux;
        subservicio = it.subservventa;
        proveedor = it.proveedorAux;
        log.error('** eventoEN = ' + eventoENP)
        //suma = suma+value1+value2+value3+value4+value5
        if (eventoENP == 'M') {
            log.error('Value p='+p+', value xxx='+xxx)
            if(p < cont){
                p++
				idsENPs = ENPs[p]
				//idsENPs = idsENP
                event = 'M'
				log.warn('Entra Modifica '+p+' veces')
                if(idsENPs[0].toString() == 'N'){
					log.error('idsENPs 0 -->'+idsENPs[4,5,6,7,8,9,10,11,12,13,14])
					def numIdEnp = 9502 + idsENPs[4,5,6,7,8,9,10,11,12,13,14]
					log.warn('Valor de idsENPs->'+idsENPs+' y de numIdEnp->'+numIdEnp)
					idsENPs = numIdEnp
				}
			}else{
				idsENPs = ''
                event = 'A'
				return 'No hay más ENP que poner'
            }
            log.warn('Modifica ' + p + ' veces')
			log.warn('Salimos del if-->'+idsENPs)
            Map < String, Object > row = new HashMap < String, Object > ();
            row.put("tipopep", 'ENP')
            row.put("IDpep", idsENPs != null ? idsENPs : '')
            row.put("actividadAux", it.actividadAux != null ? it.actividadAux : '')
            row.put("nombreactividad", it.nombreactividad != null ? it.nombreactividad : '')
            row.put("codactividad", it.codactividad)
            row.put("nombrepepaux", it.nombrepepaux != null ? it.nombrepepaux : '')
            row.put("unidaditAux", unidadit)
            row.put("grupoartAux", grupoart != null ? grupoart : '-')
            row.put("subservventaAux", subservicio != null ? it.subservicio : '-')
            row.put("proveedorAux", proveedor != null ? proveedor : '-')
            row.put("y1aux", value1 != null ? value1 : 0)
            row.put("y2aux", value2 != null ? value2 : 0)
            row.put("y3aux", value3 != null ? value3 : 0)
            row.put("y4aux", value4 != null ? value4 : 0)
            row.put("y5aux", value5 != null ? value5 : 0)
            row.put("soly1", valueS1) //MFS-3   guardamos solicitados para el buffer
            row.put("soly2", valueS2)
            row.put("soly3", valueS3)
            row.put("soly4", valueS4)
            row.put("soly5", valueS5)
            row.put("evento", event);
            row.put("year1", it.year1);
            row.put("ceco", it.ceco != null ? it.ceco : '')
            row.put("gicaux", it.gic != null ? it.gic : '')
            row.put("pd", 'Si')
            row.put("codigosociedad", it.codigosociedad)
            row.put("grupo", it.grupo)
            row.put("escapex", it.escapex)
            row.put("prov", it.prov);
            row.put("sscte", it.sscte)
            row.put("ssvta", it.ssvat);
            row.put("nivelsup", it.nivelsup); // it.codactividad
            row.put("cliente", it.cliente);
            row.put("costowner", it.costowner);
            log.error('Se modifica el ENP existente')
            gh.insertRowGrid(issue.getId(), gridID, user, row)
            gh.insertRowGrid(issue.getId(), gridIDPEP, user, row)
        }/*else {
            log.error('EVENTO = A')
            Map < String, Object > row = new HashMap < String, Object > ();
            row.put("tipopep", 'ENP')
            //row.put("IDpep", idsENP != null ? idsENP : '')
            row.put("IDpep", idsENPs != null ? idsENPs : '')
            row.put("actividadAux", it.actividadAux != null ? it.actividadAux : '')
            row.put("nombreactividad", it.nombreactividad != null ? it.nombreactividad : '')
            row.put("codactividad", it.codactividad)
            row.put("nombrepepaux", '')
            row.put("unidaditAux", unidadit)
            row.put("grupoartAux", grupoart != null ? grupoart : '-')
            row.put("subservventaAux", subservicio != null ? it.subservicio : '-')
            row.put("proveedorAux", proveedor != null ? proveedor : '-')
            row.put("y1aux", value1 != null ? value1 : 0)
            row.put("y2aux", value2 != null ? value2 : 0)
            row.put("y3aux", value3 != null ? value3 : 0)
            row.put("y4aux", value4 != null ? value4 : 0)
            row.put("y5aux", value5 != null ? value5 : 0)
            row.put("soly1", valueS1) //MFS-3   guardamos solicitados para el buffer
            row.put("soly2", valueS2)
            row.put("soly3", valueS3)
            row.put("soly4", valueS4)
            row.put("soly5", valueS5)
            row.put("evento", 'A');
            row.put("year1", it.year1);
            row.put("ceco", it.ceco != null ? it.ceco : '')
            row.put("gicaux", it.gic != null ? it.gic : '')
            row.put("pd", 'Si')
            row.put("codigosociedad", it.codigosociedad)
            row.put("grupo", it.grupo)
            row.put("escapex", it.escapex)
            row.put("prov", it.prov);
            row.put("sscte", it.sscte)
            row.put("ssvta", it.ssvat);
            row.put("nivelsup", it.nivelsup); // it.codactividad
            row.put("cliente", it.cliente);
            row.put("costowner", it.costowner);
            log.error('XXX Se crea una linea nueva de ENP')
            gh.insertRowGrid(issue.getId(), gridID, user, row)
            gh.insertRowGrid(issue.getId(), gridIDPEP, user, row)

            log.error('YYY Se crea una linea nueva de ENP')
        }*/
    } else {
    log.error('XX No hay SOF con lo cual esta linea no la modifica')
    }

}

gridDataPEP.each() //por cada Linea de Coste del Grid One Time
{
    log.error('it.tipopep.name-->' + it.tipopep.name)
    if (it.tipopep.name == 'ENP') log.error('Tipo = ' + it.tipopep.name + ', sol1=' + it.soly1 + ', sol2=' + it.soly2 + ', sol3=' + it.soly3 + ', sol4=' + it.soly4)
}
//sleep(500)
reindexar(issue)
def reindexar(def issue_r) {
    try {
        def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
        boolean wasIndexing = ImportUtils.isIndexIssues();

        ImportUtils.setIndexIssues(true);
        issueIndexingService.reIndex(issue_r)
        ImportUtils.setIndexIssues(wasIndexing)

        return true
    } catch (Exception e) {
        log.error("$issue_r: Error en reindexado $e");
        return false
    }
}