import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import com.atlassian.jira.component.ComponentAccessor
import groovy.transform.Field
//sleep(9000)

customFieldManager = ComponentAccessor.customFieldManager
issueManager = ComponentAccessor.issueManager
def issueConfig 			= issueManager.getIssueObject("CH-2")

cfActivoClarity = customFieldManager.getCustomFieldObject("customfield_15010")
def pluginAccessor = ComponentAccessor.getPluginAccessor();
def plugin = pluginAccessor.getPlugin("com.valiantys.jira.plugins.SQLFeed");
def serviceClass = plugin.getClassLoader().loadClass("com.valiantys.nfeed.api.IFieldValueService");
fieldValueService = ComponentAccessor.getOSGiComponentInstanceOfType(serviceClass)
Object userAdmin = ComponentAccessor.userManager.getUserByName("ajira")
Object userObject = ComponentAccessor.jiraAuthenticationContext.getLoggedInUser();
def user = userObject instanceof ApplicationUser ? ((ApplicationUser) userObject).getDirectoryUser() : (User) userObject
def userAdminTable = userAdmin instanceof ApplicationUser ? ((ApplicationUser) userAdmin).getDirectoryUser() : (User) userAdmin
def dataManagerClass = pluginAccessor.getClassLoader().findClass("com.idalko.jira.plugins.igrid.api.data.TGEGridTableDataManager");
tgeGridDataManager = ComponentAccessor.getOSGiComponentInstanceOfType(dataManagerClass);
int sum = 0
def cfParametrosCostes 		= customFieldManager.getCustomFieldObject("customfield_15300") 	//Cambia entre DES y PRO!!!
def cfParametrosCostesHg 	= customFieldManager.getCustomFieldObject("customfield_18004")	//Cambia entre DES y PRO!!!
def cfExcepcion 		= customFieldManager.getCustomFieldObject(16905L)
//def cfCategoria 		= customFieldManager.getCustomFieldObject(11500L)
def cfCategoria = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(11500L)
def cfAplicaHoraGestion = customFieldManager.getCustomFieldObject(18102L)

configMap 	= loadConfig(issueConfig, userAdminTable, cfParametrosCostes.getIdAsLong())
configMap2 	= loadConfig(issueConfig, userAdminTable, cfParametrosCostesHg.getIdAsLong())

/**************************Consols*************************************************************************/
//issue = issueManager.getIssueObject("PPR-9605")//9444")
/**************************Consols*************************************************************************/
def categoriaValue 		= issue.getCustomFieldValue(cfCategoria).optionId
def excepcionValue 		= issue.getCustomFieldValue(cfExcepcion)
def aHoraGestionValue  	= issue.getCustomFieldValue(cfAplicaHoraGestion)
//def unidadIt78			= "7841 – Administración"


if(categoriaValue == 14003L ){ // categoriaValue == 10903L || categoriaValue == 10901L || || categoriaValue == 11130L){
    return
}

def cfPlanValue 			= getNfeedValue(issue, "customfield_11607") //Plan
def costesIdAsString 		= "customfield_18221" // costes table grid (DEL PPR)
def costesField 			= customFieldManager.getCustomFieldObject(costesIdAsString);
Long costesIDAsLong 		= costesField.getIdAsLong()
def costesGridData 			= readGridDataXD(issue, user, costesIDAsLong)


idCostesIndirectos = []
def listRows = getTableRows(costesGridData)
log.warn('Lista-->'+listRows)
if(idCostesIndirectos.size() > 0){
    deleteAllConditionalRows(issue, costesIDAsLong, idCostesIndirectos.toSet(), user)
}

def vy1 = 0d
for(row in listRows){  //  (listRows) costes del PPR
    log.error('entra??????')
    vy1 = row.y1.toInteger()+row.y2.toInteger()+row.y3.toInteger()+row.y4.toInteger()+row.y5.toInteger()
    log.error('Valor del proyecto solicitado-->'+vy1+' de un tipo pep '+row.tipopep.value)
    //if (row.tipopep.name == 'SOF'){
    sum = (vy1+sum)
    log.error('Suma-->'+sum)
    //}
}
groupListManually = ["year1":0d, "year2":0d, "year3":0d, "year4":0d, "year5":0d]
for(row in listRows){  //  (listRows) costes del PPR
    if(row.tipopep.value == "SOF" && sum > 100000){  // naturaleza del coste PPR y total solicitado
        groupListManually.put("year1", groupListManually.get("year1") + row.y1aux)
        groupListManually.put("year2", groupListManually.get("year2") + row.y2aux)
        groupListManually.put("year3", groupListManually.get("year3") + row.y3aux)
        groupListManually.put("year4", groupListManually.get("year4") + row.y4aux)
        groupListManually.put("year5", groupListManually.get("year5") + row.y5aux)
    }
}

def groupListManually2 = []
def groupListManually3 = []
log.error('excepcionValue-->'+excepcionValue)
if (excepcionValue == null){
// Costes (Entornos No Productivos > 100k)
    log.error('groupListManually.year1('+groupListManually.year1+' > configMap.get("Entornos No Productivos")[4]('+configMap.get("Entornos No Productivos")[4]+')')
    log.error('groupListManually.year2('+groupListManually.year2+' > configMap.get("Entornos No Productivos")[4]('+configMap.get("Entornos No Productivos")[4]+')')
    log.error('groupListManually.year3('+groupListManually.year3+' > configMap.get("Entornos No Productivos")[4]('+configMap.get("Entornos No Productivos")[4]+')')
    log.error('groupListManually.year4('+groupListManually.year4+' > configMap.get("Entornos No Productivos")[4]('+configMap.get("Entornos No Productivos")[4]+')')
    log.error('groupListManually.year5('+groupListManually.year5+' > configMap.get("Entornos No Productivos")[4]('+configMap.get("Entornos No Productivos")[4]+')')
    log.error('Suma ('+sum+') > 1000000')
    if(	groupListManually.year1 > configMap.get("Entornos No Productivos")[4] ||
            groupListManually.year2 > configMap.get("Entornos No Productivos")[4] ||
            groupListManually.year3 > configMap.get("Entornos No Productivos")[4] ||
            groupListManually.year4 > configMap.get("Entornos No Productivos")[4] ||
            groupListManually.year5 > configMap.get("Entornos No Productivos")[4] ||
            sum > 100000){
        for(row in listRows){
            log.error('1 Valor del proyecto solicitado-->'+vy1+' de un tipo pep '+row.tipopep.value)
            log.error('La suma es '+sum)
            if(row.tipopep.value == "SOF" && sum > 100000){  // naturaleza del coste PPR y total solicitado
                def isFound = false
                log.warn('Crea un ENP del '+row.tipopep.value)
                for (int i = 0; i < groupListManually2.size() ; i++) {
                    def grouped = groupListManually2.get(i)
                    if(row.codactividad == grouped.codactividad){
                        isFound = true
                        groupListManually2[i].calculoY1 = grouped.calculoY1 + row.y1aux
                        groupListManually2[i].calculoY2 = grouped.calculoY2 + row.y2aux
                        groupListManually2[i].calculoY3 = grouped.calculoY3 + row.y3aux
                        groupListManually2[i].calculoY4 = grouped.calculoY4 + row.y4aux
                        groupListManually2[i].calculoY5 = grouped.calculoY5 + row.y5aux
                        break
                    }
                }
                if(!isFound){
                    def internalRow = row.clone()
                    internalRow.put("calculoY1", row.y1aux)
                    internalRow.put("calculoY2", row.y2aux)
                    internalRow.put("calculoY3", row.y3aux)
                    internalRow.put("calculoY4", row.y4aux)
                    internalRow.put("calculoY5", row.y5aux)
                    groupListManually2.add(internalRow)
                }
            }
        }
    }
}
// Limitar planes de descarte
// Flag de aplicar este Fee   (Modificar este flag por un Checkbox)
// Costes (Horas gestion > 100k)
// Transformación Tecnológica [77548]
// Tecnología [77637]

/*if(aHoraGestionValue == null && (cfPlanValue != "24557" && cfPlanValue != "25113")){
    log.error("llego aqui???????????????x?????????? ${groupListManually.year1}")
    if(	groupListManually.year1 > configMap2.get("Horas gestion")[4] ||
            groupListManually.year2 > configMap2.get("Horas gestion")[4] ||
            groupListManually.year3 > configMap2.get("Horas gestion")[4] ||
            groupListManually.year4 > configMap2.get("Horas gestion")[4] ||
            groupListManually.year5 > configMap2.get("Horas gestion")[4]){
        for(row in listRows){
            if(row.tipopep.value == "SOF"){
                def isFound = null
                for (int i = 0; i < groupListManually3.size() ; i++) {
                    def grouped = groupListManually3.get(i)
                    if(row.codactividad == grouped.codactividad){
                        isFound = grouped
                        groupListManually3[i].year1 = grouped.year1 + row.year1
                        groupListManually3[i].year2 = grouped.year2 + row.year2
                        groupListManually3[i].year3 = grouped.year3 + row.year3
                        groupListManually3[i].year4 = grouped.year4 + row.year4
                        groupListManually3[i].year5 = grouped.year5 + row.year5
                        break
                    }
                }
                if(isFound == null){
                    groupListManually3.add(row.clone())
                }
            }
        }
    }
}
*/
for (group in groupListManually2){
    log.error("Hola1 ${group}")
    try{
        createRow(issue, costesIDAsLong, group, user)
    } catch (Exception e){
        log.error("Error $e")
    }
}

log.error("Countt ${groupListManually3.size()}")
for (group in groupListManually3){
    log.error("Hola2")
    try{
        createRowGestion(issue, costesIDAsLong, group, user)
    } catch (Exception e){
        log.error("Error $e")
    }
}
void deleteAllConditionalRows(def issue, def tgeCustomFieldId, def rowIdsToDelete, def user){
    tgeGridDataManager.deleteRows(issue.getId(), tgeCustomFieldId, rowIdsToDelete, user)
    log.error("Deleted rows: ${rowIdsToDelete}")
}

def createRowGestion(def issue, def tgeCustomFieldId, def result, def user) throws Exception{
    def cfTotalSolicitado		= customFieldManager.getCustomFieldObject(12659L)
    def totalSolicitado			= issue.getCustomFieldValue(cfTotalSolicitado)
    log.error("Esto que da y esto que da ${configMap2.get("Horas gestion")?.getAt(4)} and ${configMap2.get("Horas gestion")?.getAt(5)}")
    def y1Value = (totalSolicitado > configMap2.get("Horas gestion")[4] ? result.year1 * configMap2.get("Horas gestion")[5] / 100 : 0)
    def y2Value = (totalSolicitado > configMap2.get("Horas gestion")[4] ? result.year2 * configMap2.get("Horas gestion")[5] / 100 : 0)
    def y3Value = (totalSolicitado > configMap2.get("Horas gestion")[4] ? result.year3 * configMap2.get("Horas gestion")[5] / 100 : 0)
    def y4Value = (totalSolicitado > configMap2.get("Horas gestion")[4] ? result.year4 * configMap2.get("Horas gestion")[5] / 100 : 0)
    def y5Value = (totalSolicitado > configMap2.get("Horas gestion")[4] ? result.year5 * configMap2.get("Horas gestion")[5] / 100 : 0)

    log.error("y aqui?")
    if(y1Value == 0 && y2Value == 0 && y3Value == 0 && y4Value == 0 && y5Value == 0){
        return
    }

    log.error("y aqui? no creo...")
    Map<String, Object> row = new HashMap<String, Object>();
    row.put("concepto", "Horas gestión")
    row.put("unidad", 7881/*result?.unidad?.value*/)
    log.warn("hola")
    row.put("naturaleza", "b_dedi_int_proy_manag")
    row.put("year1", y1Value)
    row.put("year2", y2Value)
    row.put("year3", y3Value)
    row.put("year4", y4Value)
    row.put("year5", y5Value)
    row.put("actividad", result.actividad.value)
    row.put("planned", "Si")
    row.put("indirecto", "1")
    row.put("subservicio", -1)
    log.error("test")
    List<Long> rowIds = tgeGridDataManager.addRows(issue.getId(), tgeCustomFieldId, Arrays.asList(row), user);
    log.error("test2")

}

def createRow(def issue, def tgeCustomFieldId, def result, def user) throws Exception{
    log.error("Result: ${result}")
    def cfTotalSolicitado		= customFieldManager.getCustomFieldObject(12659L)
    def totalSolicitado			= issue.getCustomFieldValue(cfTotalSolicitado)
    def y1Value = (totalSolicitado > configMap.get("Entornos No Productivos")[4] ? result.calculoY1 * configMap.get("Entornos No Productivos")[5] / 100 : 0)
    def y2Value = (totalSolicitado > configMap.get("Entornos No Productivos")[4] ? result.calculoY2 * configMap.get("Entornos No Productivos")[5] / 100 : 0)
    def y3Value = (totalSolicitado > configMap.get("Entornos No Productivos")[4] ? result.calculoY3 * configMap.get("Entornos No Productivos")[5] / 100 : 0)
    def y4Value = (totalSolicitado > configMap.get("Entornos No Productivos")[4] ? result.calculoY4 * configMap.get("Entornos No Productivos")[5] / 100 : 0)
    def y5Value = (totalSolicitado > configMap.get("Entornos No Productivos")[4] ? result.calculoY5 * configMap.get("Entornos No Productivos")[5] / 100 : 0)


    if(y1Value == 0 && y2Value == 0 && y3Value == 0 && y4Value == 0 && y5Value == 0){
        return
    }


    Map<String, Object> row = new HashMap<String, Object>();

    row.put("actividadAux", result.nombreactividad)
    row.put("unidaditAux", '7841 – Administración');
    //row.put("unidaditAux", unidadIt78);
    row.put("grupoartAux", result.grupoartAux);
    row.put("subservventaAux", result.subservventaAux)
    row.put("proveedorAux", result.proveedorAux)
    row.put("tipopep", "ENP");
    row.put("nombrepepaux", result.nombrepepaux);
    row.put("y1aux", y1Value);
    row.put("y2aux", y2Value);
    row.put("y3aux", y3Value);
    row.put("y4aux", y4Value);
    row.put("y5aux", y5Value);
    row.put("ceco",  result.ceco);
    row.put("pd", "Si");
    row.put("cliente", result.cliente);
    row.put("costowner", result.costowner);
    row.put("nivelsup", result.codactividad);
    //row.put("year1", result.inityear);
    row.put("year1", result.year1);
    row.put("ssvta", result.ssvat);
    //row.put("sscte", result.grupoart.value);
    row.put("sscte", result.sscte);

    row.put("prov", result.prov);
    row.put("evento", "A");
    row.put("codigosociedad",result.codigosociedad)
    row.put("grupo", result.grupo)
    row.put("escapex", result.escapex)
    row.put("nombreactividad", result.nombreactividad)
    row.put("codactividad", result.codactividad)
    row.put("indirecto", "1")

    log.error("test1.2")
    List<Long> rowIds = tgeGridDataManager.addRows(issue.getId(), tgeCustomFieldId, Arrays.asList(row), user);
    log.error("test2")
}


private getTableRows(List<Map<String, Object>> hitosGridData) {
    def listRows = []
    for (int rowNumber = 0; rowNumber < hitosGridData.size(); rowNumber++) {
        Map<String, Object> rowData = hitosGridData.get(rowNumber)
        log.error rowData
        def codActividad = rowData.get("codactividad") ?: getIdFromActividadSummary(issue, rowData.actividad)?.id
        def actividadId = Long.valueOf(codActividad)
        def actividadIssue = issueManager.getIssueObject(actividadId)
        log.error('----------------'+rowData)
        if(rowData.get("indirecto") == "1"){
            idCostesIndirectos.add(rowData.get("id"))
        } else {
            listRows.add(rowData)
//listRows.add(new Row(rowData.get("id"), rowData.get("actividad").value, rowData.get("naturaleza").value, rowData.get("cliente").value, rowData.get("subsistema").value, rowData.get("year1"), rowData.get("year2"), rowData.get("year3"), rowData.get("year4"), rowData.get("year5")))
       }
    }
	log.error('dentro--->>>'+listRows)
    return listRows
}

private readGridDataXD(def issue, def user, def fieldID) throws Exception {
    List<Map<String, Object>> gridDataList = new ArrayList<Map<String, Object>>();
    def callResult = tgeGridDataManager.readGridData(issue.getId(), fieldID, null, null, 0, tgeGridDataManager.getRowCount(issue.id, fieldID, user), user);
    gridDataList = callResult.getValues();
    return gridDataList
}

def loadConfig(def issueConfig, def user, def configIdAsLong){
    def configList = [:]
    def configGridData = readGridDataXD(issueConfig, user, configIdAsLong)
    for (int rowNumber = 0; rowNumber < configGridData.size(); rowNumber++) {
        Map<String, Object> rowData = configGridData.get(rowNumber)
        if(rowData.get("iactivo") == 1 && rowData.get("ifechaactivacion") <= new Date().time){
            configList.put(rowData.get("inomregla"), [rowData.get("inomregla"), rowData.get("ialcance"), rowData.get("itipologia"), rowData.get("ivalor"), rowData.get("iimporte"), rowData.get("irepercutir"), rowData.get("iactivo"), rowData.get("ifechaactivacion")])
        }
    }
    return configList
}

def getNfeedValue(def issue, def customField){
    for (def option: fieldValueService.getFieldValues(issue.getKey(), customField)) {
        return option
    }
    return null
}

//if (actividad.getIssueType().getId() == "10206" && actividad.getStatusId() == "10911"){

def getIdFromActividadSummary(MutableIssue issue, def summary){
    return issue.subTaskObjects.stream()
            .filter{act -> act.issueTypeId == "10206"}
            .filter{act -> act.summary == summary}
            .findFirst()
            .orElse(null)
}