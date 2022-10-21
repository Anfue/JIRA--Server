/*Creacion de subtareas desde tabla // asignados fijos usando BBDD*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.issue.IssueInputParameters
import com.atlassian.jira.issue.IssueInputParametersImpl
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.workflow.TransitionOptions
import com.atlassian.jira.issue.IssueManager;
import groovy.sql.Sql
import java.sql.Connection
import java.sql.Driver
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.crowd.embedded.api.User
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.issue.CustomFieldManager
import java.sql.SQLException
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.util.ImportUtils
import Helper.NfeedHelper  //MET-1081
import groovy.transform.Field

NfeedHelper  nfh = new NfeedHelper()

@Field CONFIG_AGRUPADOR_NONIT_OPEX = 14943    //MET-897
@Field CONFIG_AGRUPADOR_RECURSOS_OPEX = 15001 //MET-897
@Field IT_AGRUPACION_DEMANDA = 14725 //MET-1145

//***************************MET-1081
//PRO
def APLICATIVA = 12601L
def  TECNICA =   12600L
def cfCategoria = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_11500")
def proy =  nfh.getNfeedValue(issue,11507)
def CR = 10903L

def PLAN = ComponentAccessor.getIssueManager().getIssueByCurrentKey("PT-9").id   //MET-1122

def categoria = issue.getCustomFieldValue(cfCategoria)?.optionId
if(categoria == CR)
{
    categoria = ComponentAccessor.getIssueManager().getIssueObject(proy).getCustomFieldValue(cfCategoria)?.optionId
}
mejora = (categoria == APLICATIVA || categoria == TECNICA)
//***************************MET-1081

//managers
def userGeneral = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
log.error(userGeneral.name + " | " + userGeneral.key)
issueService = ComponentAccessor.getIssueService();
customFieldManager = ComponentAccessor.getOSGiComponentInstanceOfType(CustomFieldManager.class);
issueManager = ComponentAccessor.getOSGiComponentInstanceOfType(IssueManager.class);
subTaskManager = ComponentAccessor.getSubTaskManager()
def pluginAccessor = ComponentAccessor.getPluginAccessor();
Object plugin = pluginAccessor.getPlugin("com.valiantys.jira.plugins.SQLFeed");
Class serviceClass = plugin.getClassLoader().loadClass("com.valiantys.nfeed.api.IFieldDisplayService");
ComponentManager componentManager = ComponentManager.getInstance();
Object fieldDisplayService = ComponentAccessor.getOSGiComponentInstanceOfType(serviceClass);
def serviceClassNfeed = plugin.getClassLoader().loadClass("com.valiantys.nfeed.api.IFieldValueService");
def fieldValueService = ComponentAccessor.getOSGiComponentInstanceOfType(serviceClassNfeed);

//constants
TABLEID ="customfield_11726";
TIPO_SUBTAREAID = "10207"
PROY_ID = 10300L
def COSTEID = 12215
def ISSUECONFIG = "CH-2"
def TABLEOBLIGATORIO = "customfield_12610"
def VALIDATORS = "customfield_12414"
def VISAREACLI = "customfield_12318"
def ASSIGNEEORIGINALVALID = "customfield_14307"
def ITMANID = 10001
def VALIDATIONID = 12605
def FTE = 13500
def FTEECO = 13501
def tablaCostesID = 11778L
def validadorCDOID = 14801L

//table field
tgeCustomField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(TABLEID);
def cf_costes = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(COSTEID)
def planesCapacidadesDes = ["PT-126", "PT-166", "PT-168", "PT-9"]
def planesvendorManagement = ["PT-126", "PT-166", "PT-168"]

//fields
issueConfigObject = issueManager.getIssueObject(ISSUECONFIG)
def tableObligatorioField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(TABLEOBLIGATORIO)
valAssigneeOriginal = ComponentAccessor.customFieldManager.getCustomFieldObject(ASSIGNEEORIGINALVALID)
visAreaCliField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(VISAREACLI)
cf_itman = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(ITMANID)
cf_validation_unit =  ComponentAccessor.getCustomFieldManager().getCustomFieldObject(VALIDATIONID)
cf_fte = ComponentAccessor.customFieldManager.getCustomFieldObject(FTE)
cf_fteeco = ComponentAccessor.customFieldManager.getCustomFieldObject(FTEECO)
cfObligatorio = ComponentAccessor.customFieldManager.getCustomFieldObject(14922L)
cfValidadorCDO = ComponentAccessor.customFieldManager.getCustomFieldObject(validadorCDOID)
cfCodClarity = ComponentAccessor.customFieldManager.getCustomFieldObject(11720L)

//getting issue (just for console test)
//issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("PPR-6253")
credentials = getDBCredentials("halley")

def val_totalopex = 0
def val_totalcapex = 0
def val_totalCapexAprobado = 0
def val_totalOpexAprobado = 0
def opexNoPdValue = 0
val_cost = (issue.getCustomFieldValue(cf_costes) != null ? issue.getCustomFieldValue(cf_costes) : 0 )
def MAXCAPEXID = 12417
def MAXCAPEX = issueConfigObject.getCustomFieldValue(ComponentAccessor.getCustomFieldManager().getCustomFieldObject(MAXCAPEXID))

if(val_cost != 0)
{
    val_cost = val_cost.replace("[","").replace("]","").split(",")
    val_totalcapex = val_cost.size() < 49 ? Double.parseDouble(val_cost[5]) : Double.parseDouble(val_cost[49])
    val_totalCapexAprobado = Double.parseDouble(val_cost[13])
    val_totalOpexAprobado = Double.parseDouble(val_cost[14])
    val_totalopex =  Double.parseDouble(val_cost[11])
    opexNoPdValue = Double.parseDouble(val_cost[27])
}

validadoresTabla = new ArrayList()
validadoresCrear = new ArrayList()
def campoResponsables = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(VALIDATORS)
def validadoresObligatorios = []//issueConfigObject.getCustomFieldValue(tableObligatorioField)

Object displayResult = fieldDisplayService.getDisplayResultOverrideSecurity(issueConfigObject.getKey(), tableObligatorioField.getId());

if (getNfeedValue(issue, "customfield_11607", fieldValueService) != String.valueOf(PLAN)){
    //validadoresObligatorios = displayResult.getDisplays()
    validadoresObligatorios = mejora? [] : displayResult.getDisplays() //MET-1081
}

log.warn("validadoresObligatorios: " + validadoresObligatorios)

def visAreaCliValue = issue.getCustomFieldValue(visAreaCliField)
summaryBC = issue.summary
priorityId = issue.getPriority().getId();
listAssignee = []
responsables = [] //listado de validaciones a las que se le ha indicado un responsable, contiene nodos con validaciony responsable
log.warn("Priority: ${priorityId} ");

//user to execute actions
//def user = ComponentAccessor.getJiraAuthenticationContext().getUser().getDirectoryUser()
userTable = userGeneral instanceof ApplicationUser ? ((ApplicationUser) userGeneral).getDirectoryUser() : (User) userGeneral
realCurrentUser = ComponentAccessor.getJiraAuthenticationContext().getUser()
def ac = ComponentAccessor.getJiraAuthenticationContext();
user = ComponentAccessor.getUserManager().getUserByName("ajira")
userTableAdmin = user instanceof ApplicationUser ? ((ApplicationUser) user).getDirectoryUser() : (User) user
ac.setLoggedInUser(user);

//leer tabla
// get TGE custom field

Long tgeCustomFieldId = tgeCustomField.getIdAsLong();
Long tablaCostedId = 11778

// read grid data
Class dataManagerClass = pluginAccessor.getClassLoader().findClass("com.idalko.jira.plugins.igrid.api.data.TGEGridTableDataManager");
tgeGridDataManager = ComponentAccessor.getOSGiComponentInstanceOfType(dataManagerClass);
StringBuilder datosTabla = new StringBuilder();
def tabla = null
def tablaCostes = null

try {
    tabla = tgeGridDataManager.readGridData(issue.id, tgeCustomFieldId, null, null, 0, tgeGridDataManager.getRowCount(issue.id, tgeCustomFieldId,userTable), userTable)
} catch (Exception e) {
    //return e.getMessage()
    log.warn("Grid ID=" + tgeCustomFieldId + " data cannot be retrieved: " + e.getMessage() + "\n");
}

try {
    tablaCostes = tgeGridDataManager.readGridData(issue.id, tablaCostesID, null, null, 0, tgeGridDataManager.getRowCount(issue.id, tablaCostesID, userTable), userTable)
} catch (Exception e) {
    //return e.getMessage()
    log.warn("Grid ID=" + tgeCustomFieldId + " data cannot be retrieved: " + e.getMessage() + "\n");
}
def noIt = false

for(def value in tabla.getValues())
{
    log.warn(value)
    //   if(value.responsable.value != ""){
    if(value.responsable.value != "" && value.responsable.value != null){
        def username = value.responsable.value.substring( value.responsable.value.indexOf(',')+1,value.responsable.value.length())
        log.warn("username: " + username)
        log.warn("unidad tabla " +value.unidad.name)

        validadoresTabla.add(value.unidad.name)
        responsables.add([value.unidad.name,username])
    }
}

def opexNoPD = false
def opexNoItNoPDValue = 0
def opexRRHHNoPDValue = 0
def opexNoPDValue = 0

for(def value in tablaCostes.getValues())
{
    log.error("VALUE ${value}")
    if (isNoITClarity(value.naturaleza.value)){
        noIt = true
    }
    if(value.capexopex == "OPEX" && value.planned.value == "No" && value.totalcost > 0){
        opexNoPD = true
    }
    if (value.capexopex == "OPEX" && value.planned.value == "No" && esNONITopex(value.naturaleza.name)){
        opexNoItNoPDValue += value.totalcost
    } else if (value.capexopex == "OPEX" && value.planned.value == "No" && esNONITrecursos(value.naturaleza.name)){
        opexRRHHNoPDValue += value.totalcost
    }
}

log.error("${val_totalcapex+val_totalCapexAprobado} y max es ${MAXCAPEX}")
def SP = capexInferiorA(val_totalcapex, MAXCAPEX) //cambio por MET-361

List<MutableIssue> validadoresActuales = issue.subTaskObjects.stream()
        .filter{subtask -> subtask.issueTypeId == "10207"}
        .toArray()
        .toList()

List<MutableIssue> actividades = issue.subTaskObjects.stream()
        .filter{subtask -> subtask.issueTypeId == "10206"}
        .toArray()
        .toList()

def actividadWApli = actividades.stream()
        .any{activity -> getNfeedValue(activity, "customfield_11711", fieldValueService) == "ESSAB_NUEVA_APLICACION"}

def validadoresNoNecesarios = []
//if (getNfeedValue(issue, "customfield_11607", fieldValueService) != String.valueOf(PLAN)){
//validadoresObligatorios.add("Finance SABIS") MFS
//}
//MFS
/*
if(getNfeedValue(issue, "customfield_11607", fieldValueService) != String.valueOf(PLAN) && val_totalopex != 0 ){ // MET-1122
    validadoresObligatorios.add("OpEx")
} else {
    validadoresNoNecesarios.add("OpEx")
}*/
//  if (actividadWApli  && !mejora){     //MET-1084
//      validadoresObligatorios.add("Arquitectura")
//  } else {
//      validadoresNoNecesarios.add("Arquitectura")
//  }
if ( (val_totalopex+val_totalcapex+val_totalCapexAprobado+val_totalOpexAprobado > 2000 && getNfeedValue(issue, "customfield_11607", fieldValueService) != String.valueOf(PLAN))  && !mejora) {
    validadoresObligatorios.add("Dirección Financiera")
} else {
    validadoresNoNecesarios.add("Dirección Financiera")
}
/* MFS
if ( (val_totalopex+val_totalcapex+val_totalCapexAprobado+val_totalOpexAprobado) > 2000 || (val_totalopex+val_totalOpexAprobado) > 500 || opexNoItNoPDValue > 0 || opexRRHHNoPDValue > 0 || opexNoPdValue > 150){
    validadoresObligatorios.add("Organización")
}
*/
if ( (getNfeedValue(issue, "customfield_11607", fieldValueService) != String.valueOf(PLAN) && ( (issue.getCustomFieldValue(cf_fte) != null && issue.getCustomFieldValue(cf_fte) != 0)  || (issue.getCustomFieldValue(cf_fteeco) != null && issue.getCustomFieldValue(cf_fteeco) != 0) ))  && !mejora ) {
validadoresObligatorios.add("RRHH")
   
    //</MET-1145>
} else {
    validadoresNoNecesarios.add("RRHH")
   
}
//if ( (getNfeedValue(issue, "customfield_11607", fieldValueService) == "30981" ) && !mejora){ //MET-1084
//    validadoresObligatorios.add("Consultoría Negocio")
//}else if (getNfeedValue(issue, "customfield_11607", fieldValueService) != String.valueOf(PLAN) && noIt){
//    validadoresObligatorios.add("Consultoría Negocio")
//}
if (!planesCapacidadesDes.contains(issueManager.getIssueObject(getNfeedValue(issue, "customfield_11607", fieldValueService).toLong()).key) && !mejora){
    validadoresObligatorios.add("Capacidades de Desarrollo")
} else {
    validadoresNoNecesarios.add("Capacidades de Desarrollo")
}
if ( (getNfeedValue(issue, "customfield_11607", fieldValueService) != String.valueOf(PLAN) && (cfValidadorCDO != null && issue.getCustomFieldValue(cfValidadorCDO)?.optionId == 12500) ) && !mejora ){
    validadoresObligatorios.add("CDO")
} else {
    validadoresNoNecesarios.add("CDO")
}
if(planesvendorManagement.contains(issueManager.getIssueObject(getNfeedValue(issue, "customfield_11607", fieldValueService).toLong()).key) && !SP  && !mejora){
    validadoresObligatorios.add("Contract & Vendor Management")
}
if ( (getNfeedValue(issue, "customfield_11607", fieldValueService) != String.valueOf(PLAN) && issueManager.getIssueObject(Long.valueOf(getNfeedValue(issue, "customfield_11607", fieldValueService))).getCustomFieldValue(cfCodClarity) == "PL000036" ) && !mejora){
    validadoresObligatorios.add("D. de Modelo de Planificación")
}

// ************************AGREGADO EXTRA *************************
def itagrupaciondemanda = nfh.getNfeedValue(issue, IT_AGRUPACION_DEMANDA)
// log.warn("DENTRO AQUI 3: ${itagrupaciondemanda}")
if(itagrupaciondemanda == 6074){
    validadoresObligatorios.add("M. Planing Empresas y Red")
}else if(itagrupaciondemanda == 6464){
    validadoresObligatorios.add("M. Planing Particulares")

    // HDEV-44342  11728 (Objetivo) == Normative
    def cfObjetivoValue = getNfeedValue(issue, "customfield_11728", fieldValueService)
    log.warn("VALOR Objetivo --> " + cfObjetivoValue + " - "+(cfObjetivoValue.equals("4")))
    if(cfObjetivoValue.equals("4")) {
       validadoresObligatorios.add("Asesoría Jurídica")
    }
}

// **************************************************************

//validadoresCrear = arraysUnion(validadoresObligatorios, validadoresQTabla)
//log.warn("ArrayUnion: " + validadoresCrear)
log.warn("validadoresActuales: " + validadoresActuales)
def arraySum = (validadoresTabla + validadoresObligatorios).unique()
log.error("ArraySum ${arraySum}")
def arrayAlta = []

validadoresActualesCopia = validadoresActuales
def arrayValidadoresCancelados = []
for(validador in validadoresActuales){
    log.error('validador.statusId-------------------------->'+validador.statusId)
    if(validador.statusId == "1"){
        pasarATransicion(issueService, user, validador, 171)
        arrayValidadoresCancelados.add(validador.key)
        log.warn('<------------------------validador.key---------------------->'+validador.key)
    }
}
log.error("test")
log.warn("Cancelados: ${arrayValidadoresCancelados}")


for (validador in arraySum){
    MutableIssue validationFound = null

    for (validador2 in validadoresActualesCopia){
        def validationUnit = getNfeedValue(validador2, "customfield_12605", fieldValueService)
        log.error("Voy a comparar ${validador} y ${validationUnit}")
        if (validador == validationUnit){
            validationFound =  validador2
            break
        }
    }

    if (validationFound != null){
        log.error("Validation Found me da: ${validationFound} y estado es ${validationFound.statusId}")
        if(arrayValidadoresCancelados.contains(validationFound.key) || validationFound.statusId == "10102"){
            pasarATransicion(issueService, user, validationFound, 41)
        }
    } else {
        arrayAlta.add(validador)
    }
    /*log.error("Validador: ${validador.key} | Status: ${validador.statusId}")
    if (validador.statusId != "1"){
        pasarATransicion(issueService, user, validador.id, 41)
    }

    def validationUnit = getNfeedValue(validador, "customfield_12605", fieldValueService)
    if (validadoresNoNecesarios.contains(validationUnit) || !validadoresTabla.contains(validationUnit)){
        pasarATransicion(issueService, user, validador.id, 171)
    }

    if (validadoresTabla.contains(validationUnit)){
        validadoresTabla.remove(validationUnit)
    }

    if (validadoresObligatorios.contains(validationUnit)){
        validadoresObligatorios.remove(validationUnit)
    }
    */
}
log.warn("validadoresTabla: " + validadoresTabla)
log.warn("validadoresObligatorios: " + validadoresObligatorios)
if (arrayAlta){
    crearSubtasks(arrayAlta)
}

campoResponsables.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(campoResponsables), listAssignee),new DefaultIssueChangeHolder())


def arraysUnion(ArrayList list1, ArrayList list2) {
    Set set = new HashSet();

    set.addAll(list1);
    set.addAll(list2);

    return new ArrayList(set);
}

def crearSubtasks(List validacionesCrear){

    IssueInputParameters issueIP = issueService.newIssueInputParameters();
    for(def validation : validacionesCrear){
        def mandatory_assignee = getAssignee(validation) //get te responsible of unit
        //Informar campos sistema
        issueIP.setSummary("Validando " + summaryBC);
        issueIP.setIssueTypeId(TIPO_SUBTAREAID);
        issueIP.setProjectId(PROY_ID);
        issueIP.setReporterId(realCurrentUser.getKey())
        issueIP.setPriorityId(priorityId);

        def resp = buscarresponsable(validation)
        if(resp != null)
        {
            issueIP.setAssigneeId(resp.trim())
            listAssignee.add(ComponentAccessor.getUserManager().getUserByName(resp.trim()))
        }else
        {
            log.error("###mandatory_assignee: " + mandatory_assignee)
            mandatory_assignee = ComponentAccessor.getUserManager().getUserByName(mandatory_assignee?.trim())
            if(mandatory_assignee == null)
            {
                issueIP.setAssigneeId("")
            }else
            {

                issueIP.setAssigneeId(mandatory_assignee.getKey())
                listAssignee.add(mandatory_assignee)
            }
        }



        //validador creacion ticket
        vresult = issueService.validateSubTaskCreate(user, issue.id,issueIP);
        if (vresult.isValid())
        {
            def issueValidation =  vresult.getIssue()
            IssueService.IssueResult cresult = issueService.create(user, vresult);
            subTaskManager.createSubTaskIssueLink(issue, vresult.getIssue(), user); // crear enlace tarea-subtarea
            visAreaCliField.updateValue(null, issueValidation, new ModifiedValue(issueValidation.getCustomFieldValue(visAreaCliField), issue.getCustomFieldValue(visAreaCliField)),new DefaultIssueChangeHolder())
            //a?adir campos custom: IT MANAGER y Tipo de validacion
            cf_validation_unit.updateValue(null, issueValidation, new ModifiedValue(null, validation),new DefaultIssueChangeHolder())
            cf_itman.updateValue(null, issueValidation, new ModifiedValue(null, issue.getCustomFieldValue(cf_itman)),new DefaultIssueChangeHolder())
            valAssigneeOriginal.updateValue(null, issueValidation, new ModifiedValue(issueValidation.getCustomFieldValue(valAssigneeOriginal), issueValidation.getAssignee()),new DefaultIssueChangeHolder())
            //fin a?adir campos custom
        }
        else
        {
            log.warn(  vresult.getErrorCollection());
            //return vresult.getErrorCollection()
        }
    }


}

def capexInferiorA(def val_totalcapex,def MAXCAPEX)
{
    def result = false
    if(val_totalcapex < MAXCAPEX)
    {
        result = true
    }
    return result
}

def buscarresponsable(String validacion)
{
    for(def node in responsables)
    {

        if(node[0].trim() == validacion.trim())
        {
            return node[1]
        }
    }

    return null
}

def getAssignee(String unidadValidadora)
{

    StringBuilder result = new StringBuilder();
//def tabla = null
    long tgeCustomFieldId = tgeCustomField.getIdAsLong();

    try {

        def  tabla = tgeGridDataManager.readGridData(issueConfigObject.getId(), tgeCustomFieldId, null, null, 0, tgeGridDataManager.getRowCount( issueConfigObject.id, tgeCustomFieldId,userTableAdmin), userTableAdmin);
        log.error("tgeCustomFieldId " + tgeCustomFieldId + "userTable" + userTable )
        for(def value in tabla.getValues()){
            def unidadName = value.unidad.name
            def responsableName = value.responsable.name
            String[] parts = responsableName.split(",");
            part1 = parts[1];
            if (unidadName == unidadValidadora){

                return part1;
            }

        }

    } catch (Exception e)
    {
        log.warn("Grid ID=" + tgeCustomFieldId + " data cannot be retrieved: " + e.getMessage() + "\n");
        log.error( "userTable" + userTable )
    }
    return result
}

def getNfeedValue(def issue, def customField, def fieldValueService){
    for (def option: fieldValueService.getFieldValues(issue.getKey(), customField)) {
        return option
    }
    return null
}

def getDBCredentials(def db) {
    def dbMap = [:]
    def connectionURL
    def user
    def password
    switch(db){
        case "clarity":
            connectionURL = customFieldManager.getCustomFieldObject("customfield_12647")
            user = customFieldManager.getCustomFieldObject("customfield_12648")
            password = customFieldManager.getCustomFieldObject("customfield_12649")
            break
        case "halley":
            connectionURL = customFieldManager.getCustomFieldObject("customfield_12626")
            user = customFieldManager.getCustomFieldObject("customfield_12627")
            password = customFieldManager.getCustomFieldObject("customfield_12628")
            break
    }
    dbMap.put("connection", issueConfigObject.getCustomFieldValue(connectionURL))
    dbMap.put("user", issueConfigObject.getCustomFieldValue(user))
    dbMap.put("password", issueConfigObject.getCustomFieldValue(password))
    return dbMap
}


Connection getClarityConnection() throws SQLException {
    def driver = Class.forName('oracle.jdbc.driver.OracleDriver').newInstance() as Driver
    def props = new Properties()
    def credentials = getDBCredentials("clarity")
    props.setProperty("user", credentials.get("user"))
    props.setProperty("password", credentials.get("password"))
    def conn = driver.connect(credentials.get("connection"), props) //BBDD Maestra
    return conn;
}

boolean isNoITClarity(String naturaleza) throws SQLException{
    def conn = null
    def sql = null
    try{
        conn = getClarityConnection()
        sql = new Sql(conn)
        def rows = sql.rows("SELECT AGRUP_NATURALEZA FROM BDD_CLARITY.B_V_LK_NC_TIPO_PROYECTO WHERE COD_NATURALEZA_CONTABLE = ${naturaleza}")
        if(rows.size() > 0){
            def result = rows[0].getAt(0)
            if (result == "NO-IT"){
                return true
            }
        }
        return false
    }finally {
        if (conn != null){
            conn.close()
        }
        if (sql != null){
            sql.close()
        }
    }
}

def pasarATransicion(def issueService, def userObject, MutableIssue issue, def transitionId) {
    def transitionOptions = new TransitionOptions.Builder().skipConditions().skipPermissions().build();
    def transitionValidationResult = issueService.validateTransition((ApplicationUser) userObject, issue.id, transitionId, new IssueInputParametersImpl(), transitionOptions)
    if (!transitionValidationResult.isValid()) {
        log.warn transitionValidationResult.warningCollection.warnings
        log.error transitionValidationResult.errorCollection.errorMessages
        log.error transitionValidationResult.errorCollection.errors
    } else {
        def transitionResult = issueService.transition((ApplicationUser) userObject, transitionValidationResult)
        reindexar(issue)
        if (!transitionResult.isValid()) {
            log.error("Transicion no valida")
            log.warn transitionResult.warningCollection.warnings
            log.error transitionResult.errorCollection.errorMessages
            log.error transitionResult.errorCollection.errors
        } else {
            log.error("Transicion valida")
        }
    }
}

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

def esNONITopex(String naturaleza)
{
    def cf_naturalezas = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(CONFIG_AGRUPADOR_NONIT_OPEX);
    return needDisplayValue(issueConfigObject,cf_naturalezas,true).contains(naturaleza)
}
def esNONITrecursos(String naturaleza)
{
    def cf_naturalezas = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(CONFIG_AGRUPADOR_RECURSOS_OPEX);
    return needDisplayValue(issueConfigObject,cf_naturalezas,true).contains(naturaleza)
}
def needDisplayValue(def issue, def cf, Boolean multiple)
{
    Object plugin = ComponentAccessor.getPluginAccessor().getPlugin("com.valiantys.jira.plugins.SQLFeed");
    Class serviceClass = plugin.getClassLoader().loadClass("com.valiantys.nfeed.api.IFieldDisplayService");
    ComponentManager componentManager = ComponentManager.getInstance();
    Object fieldDisplayService = ComponentAccessor.getOSGiComponentInstanceOfType(serviceClass);
    Object displayResult = fieldDisplayService.getDisplayResult(issue.getKey(), cf.getId());
    return multiple ? displayResult.getDisplays() :displayResult.getDisplay()
}