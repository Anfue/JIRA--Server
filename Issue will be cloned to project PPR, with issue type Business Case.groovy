import com.atlassian.jira.ComponentManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.IssueInputParameters
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLinkTypeManager
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import groovy.sql.Sql
import org.apache.log4j.Logger
import org.apache.log4j.Level
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.plugin.PluginAccessor
import Helper.CommonHelper
import java.sql.Driver
import java.sql.PreparedStatement
import java.sql.ResultSet
import Helper.NfeedHelper  //MET-1031


BOAUX = 11787
AREALITERAL = 11794
RESPONSABLEBC = 13105
agrupacionITID = 14925 //MET-1031

issueManager = ComponentAccessor.getIssueManager()
issueLinkTypeManager = ComponentManager.getComponentInstanceOfType(IssueLinkTypeManager.class)
linkMgr = ComponentAccessor.getIssueLinkManager()
issueConfig = issueManager.getIssueObject("CH-2")
credentials = getDBCredentials()
issueService = ComponentAccessor.getIssueService()
subTaskManager = ComponentAccessor.getSubTaskManager()
customFieldManager = ComponentAccessor.getCustomFieldManager()
userManager = ComponentAccessor.getUserManager()
authenticationContext = ComponentAccessor.getJiraAuthenticationContext()
def pluginAccessor = ComponentAccessor.getPluginAccessor();
def plugin = pluginAccessor.getPlugin("com.valiantys.jira.plugins.SQLFeed");
def serviceClass = plugin.getClassLoader().loadClass("com.valiantys.nfeed.api.IFieldValueService");
fieldValueService = ComponentAccessor.getOSGiComponentInstanceOfType(serviceClass);

userAdmin = ComponentAccessor.getUserManager().getUserByName("ajira")
//user
user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
authenticationContext.setLoggedInUser(userAdmin)
userDefaultIssue = new DefaultIssueChangeHolder()

//Objeto Demanda
demanda = sourceIssue
def cfCategoria = customFieldManager.getCustomFieldObject('customfield_11500')
def categoria = demanda.getCustomFieldValue(cfCategoria)

//Campos
cf_area_literal = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(AREALITERAL)
cf_bo_literal =   ComponentAccessor.getCustomFieldManager().getCustomFieldObject(BOAUX)
cf_responsable = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(RESPONSABLEBC)
cfFechaInicio = customFieldManager.getCustomFieldObject('customfield_11404')
cfProject = customFieldManager.getCustomFieldObject('customfield_11614')
cfPreliminary = customFieldManager.getCustomFieldObject('customfield_10226')
cfDescRiesgo = customFieldManager.getCustomFieldObject('customfield_11408')
cfFechaFin = customFieldManager.getCustomFieldObject("customfield_11705")
cfYear1 = customFieldManager.getCustomFieldObject("customfield_12706")
cfCodClarity = customFieldManager.getCustomFieldObject("customfield_11720")
def cfAssigneeOriginal = customFieldManager.getCustomFieldObject("customfield_14418")


//Valores campos comunes
descRiesgo = demanda.getCustomFieldValue(cfDescRiesgo)
int currentYear = Calendar.getInstance().get(Calendar.YEAR); //MET-1094
log.error(currentYear)//MET-1094

//Acciones comunes (con o sin Change request)
if(issue.getCustomFieldValue(cf_responsable) != null)
{
    issue.setAssigneeId(issue.getCustomFieldValue(cf_responsable).getKey())
}

issue.setCustomFieldValue(cfAssigneeOriginal, issue.getAssignee())
issue.setReporter(user)
issue.setCustomFieldValue(cfPreliminary, descRiesgo)
issue.setCustomFieldValue(cfYear1, currentYear.doubleValue())//MET-1094
issue.setCustomFieldValue(cf_area_literal,demanda.getCustomFieldValue(cf_area_literal))
issue.setCustomFieldValue(cf_bo_literal,demanda.getCustomFieldValue(cf_bo_literal))

doAfterCreate = {
    log.error("Do after create COMUN")
    fieldValueService.setFieldValue(issue.getKey(), "customfield_11716", getNfeedValue(demanda, "customfield_11607"))

    //MET-1031:
    NfeedHelper  nfh = new NfeedHelper()
    CommonHelper ch =  new CommonHelper()
    def agrupacionCF =  ComponentAccessor.getCustomFieldManager().getCustomFieldObjects(issue ).find {it.id == "customfield_${agrupacionITID}"}
    def programaCF =    ComponentAccessor.getCustomFieldManager().getCustomFieldObjects(issue ).find {it.id == 'customfield_10800'}
    def programa =      ComponentAccessor.getIssueManager().getIssueObject(issue.getCustomFieldValue(programaCF).toString().replaceAll("\\D+","").toInteger())
    // def agrupacion = nfh.nfeedDisplayValue(programa.id, "customfield_${agrupacionITID}", false)
    def agrupacion  = nfh.getNfeedValueObject(programa ,agrupacionITID)

    agrupacionCF.updateValue(null,issue, new ModifiedValue(null, (agrupacion != null ?  agrupacion.toString(): null)  ),new DefaultIssueChangeHolder());
    ch.reindexar(issue)
}
checkLink = {link -> false}

//issueDespues = null


if(!categoria.toString().contains("Change request")){ //NO es Change request
    log.error("NO CHANGE REQUEST")

    issue.setCustomFieldValue(cfProject, issue.summary)
    issue.setCustomFieldValue(cfYear1, (double) sourceIssue.getCustomFieldValue(cfFechaInicio).year + 1900)//MET-1094


}
else{ //Es Change request
    log.error("CHANGE REQUEST")
    //Custom field NFeed

    doAfterCreate = {
        log.error("Do after Create CR")
        fieldValueService.setFieldValue(issue.getKey(), "customfield_11716", getNfeedValue(demanda, "customfield_11607"))
        //Objeto BC
        log.error("Objeto BC: " + issue.id + issue.key)
        def issueBC = issue

        //Objeto Proyecto (change request)
        def cfSelecProyecto = customFieldManager.getCustomFieldObject('customfield_11507')
        def cfSummaryProjectScopesObjs = customFieldManager.getCustomFieldObject("customfield_10105") //TEXT
        def cfExistingInfrastructure = customFieldManager.getCustomFieldObject("customfield_10203") //select list
        def cfApplication = customFieldManager.getCustomFieldObject("customfield_11711") // nfeed
        def cfInstrastructureStatus = customFieldManager.getCustomFieldObject("customfield_10205") //select list
        def cfUpdatedCostIncl = customFieldManager.getCustomFieldObject("customfield_10242") //select list
        def cfCertificationComments = customFieldManager.getCustomFieldObject("customfield_10206") //text
        def cfInvolvedAreas = customFieldManager.getCustomFieldObject("customfield_10243")  //multiselect list
        def cfOtherAreas = customFieldManager.getCustomFieldObject("customfield_10244") //text
        def proyectoID = getNfeedValue(demanda, "customfield_11507") //cfSelecProyecto
        def preliminaryRisk = customFieldManager.getCustomFieldObject(10226L)
        def existingInfrastructure = customFieldManager.getCustomFieldObject(10203L)
        def application = customFieldManager.getCustomFieldObject(11711L)
        def opcionesAplicacion = customFieldManager.getCustomFieldObject(13522L)
        def certificada = customFieldManager.getCustomFieldObject(13523L)
        def homologada = customFieldManager.getCustomFieldObject(13524L)
        def critica = customFieldManager.getCustomFieldObject(13525L)
        def infrastructureStatus = customFieldManager.getCustomFieldObject(10205L)
        def updatedCostIncluded = customFieldManager.getCustomFieldObject(10242L)

        proyecto = issueManager.getIssueObject(Long.valueOf(proyectoID))
        log.error("Objecto Proyecto: " + proyecto)

        def fechaInicioProyecto = proyecto.getCustomFieldValue(cfFechaInicio)
        def fechaFinProyecto = proyecto.getCustomFieldValue(cfFechaFin)
        //cfFechaInicio.updateValue(null, demanda, new ModifiedValue("", fechaInicioProyecto),userDefaultIssue)
        def descRiesgoDEM = demanda.getCustomFieldValue(cfDescRiesgo)
        def codClarity = proyecto.getCustomFieldValue(cfCodClarity)
        //issue.setReporter(ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser())

		//MET-1116
		def fasttrackID =  17105L
		fasttrackCF =  ComponentAccessor.customFieldManager.getCustomFieldObject(fasttrackID)
		fasttrackCF.updateValue(null, issueBC, new ModifiedValue(null, proyecto.getCustomFieldValue(fasttrackCF) ),userDefaultIssue)

        //cfProject.updateValue(null, issueBC, new ModifiedValue(issueBC.getCustomFieldValue(cfProject),  proyecto.summary),userDefaultIssue)//Gestionado en el flujo BC,transicion de creacion
        cfFechaInicio.updateValue(null, issueBC, new ModifiedValue("", fechaInicioProyecto),userDefaultIssue)
        //cfFechaFin.updateValue(null, issueBC, new ModifiedValue("", fechaFinProyecto),userDefaultIssue)
        cfCodClarity.updateValue(null, issueBC, new ModifiedValue("", codClarity),userDefaultIssue)
        cfSummaryProjectScopesObjs.updateValue(null, issueBC, new ModifiedValue(issueBC.getCustomFieldValue(cfSummaryProjectScopesObjs), proyecto.getCustomFieldValue(cfSummaryProjectScopesObjs)), userDefaultIssue)
        cfExistingInfrastructure.updateValue(null, issueBC, new ModifiedValue(issueBC.getCustomFieldValue(cfExistingInfrastructure), proyecto.getCustomFieldValue(cfExistingInfrastructure)), userDefaultIssue)
        cfApplication.updateValue(null, issueBC, new ModifiedValue(issueBC.getCustomFieldValue(cfApplication), proyecto.getCustomFieldValue(cfApplication)), userDefaultIssue)
        cfInstrastructureStatus.updateValue(null, issueBC, new ModifiedValue(issueBC.getCustomFieldValue(cfInstrastructureStatus), proyecto.getCustomFieldValue(cfInstrastructureStatus)), userDefaultIssue)
        cfUpdatedCostIncl.updateValue(null, issueBC, new ModifiedValue(issueBC.getCustomFieldValue(cfUpdatedCostIncl), proyecto.getCustomFieldValue(cfUpdatedCostIncl)), userDefaultIssue)
        cfCertificationComments.updateValue(null, issueBC, new ModifiedValue(issueBC.getCustomFieldValue(cfCertificationComments), proyecto.getCustomFieldValue(cfCertificationComments)), userDefaultIssue)
        cfInvolvedAreas.updateValue(null, issueBC, new ModifiedValue(issueBC.getCustomFieldValue(cfInvolvedAreas), proyecto.getCustomFieldValue(cfInvolvedAreas)), userDefaultIssue)
        cfOtherAreas.updateValue(null, issueBC, new ModifiedValue(issueBC.getCustomFieldValue(cfOtherAreas), proyecto.getCustomFieldValue(cfOtherAreas)), userDefaultIssue)
        preliminaryRisk.updateValue(null, issueBC, new ModifiedValue(issueBC.getCustomFieldValue(preliminaryRisk), proyecto.getCustomFieldValue(preliminaryRisk)), userDefaultIssue)

        existingInfrastructure.updateValue(null, issueBC, new ModifiedValue(issueBC.getCustomFieldValue(existingInfrastructure), proyecto.getCustomFieldValue(existingInfrastructure)), userDefaultIssue)
        application.updateValue(null, issueBC, new ModifiedValue(issueBC.getCustomFieldValue(application), proyecto.getCustomFieldValue(application)), userDefaultIssue)
        opcionesAplicacion.updateValue(null, issueBC, new ModifiedValue(issueBC.getCustomFieldValue(opcionesAplicacion), proyecto.getCustomFieldValue(opcionesAplicacion)), userDefaultIssue)
        certificada.updateValue(null, issueBC, new ModifiedValue(issueBC.getCustomFieldValue(certificada), proyecto.getCustomFieldValue(certificada)), userDefaultIssue)
        homologada.updateValue(null, issueBC, new ModifiedValue(issueBC.getCustomFieldValue(homologada), proyecto.getCustomFieldValue(homologada)), userDefaultIssue)
        critica.updateValue(null, issueBC, new ModifiedValue(issueBC.getCustomFieldValue(critica), proyecto.getCustomFieldValue(critica)), userDefaultIssue)
        infrastructureStatus.updateValue(null, issueBC, new ModifiedValue(issueBC.getCustomFieldValue(infrastructureStatus), proyecto.getCustomFieldValue(infrastructureStatus)), userDefaultIssue)
        updatedCostIncluded.updateValue(null, issueBC, new ModifiedValue(issueBC.getCustomFieldValue(updatedCostIncluded), proyecto.getCustomFieldValue(updatedCostIncluded)), userDefaultIssue)



        //Creacion Actividades
        def subproyectos = proyecto.getSubTaskObjects()
        log.error("Subproyectos: " + subproyectos)
        for(def subproyecto : subproyectos){
            crearActividad(subproyecto, issueBC)
        }
        //linkBcWithProject(issueBC, proyecto)
    }
}


def getNfeedValue(def issue, def customField){
    for (def option: fieldValueService.getFieldValues(issue.getKey(), customField)) {
        return option
    }
    return null
}


def crearActividad(MutableIssue subproyecto, def issueBC){
    def issueInputParameters = issueService.newIssueInputParameters();
    issueInputParameters.setProjectId(10300L) //Proyecto id Business Case
            .setIssueTypeId("10206") //Id issue type Actividad
            .setSummary(subproyecto.summary)
            .setDescription(subproyecto.description)
            .setReporterId(subproyecto.getReporter().name)
            .setAssigneeId(subproyecto.getAssignee().name)
            .setSkipScreenCheck(true);
    //.setApplyDefaultValuesWhenParameterNotProvided(true)
    def createValidationResult = issueService.validateSubTaskCreate(userAdmin, issueBC.id, issueInputParameters);
    if (!createValidationResult.isValid()) {
        Collection<String> errors = createValidationResult.getErrorCollection().getErrorMessages();
        def errorss = createValidationResult.getErrorCollection().getErrors();
        log.error(errors);
        for (String errorr : errors){
            log.error(errorr)
        }
        for(String error : errorss)
        {
            log.error(error);
        }
        log.error("Actividad no creada!")
    } else {
        def actividad = issueService.create(userAdmin, createValidationResult).getIssue();
        subTaskManager.createSubTaskIssueLink(issueBC, actividad, userAdmin)
        subproyectoToActividadThings(subproyecto, actividad)
    }
}

def subproyectoToActividadThings(MutableIssue subproyecto, def actividad){

    def cfTipoActividad = customFieldManager.getCustomFieldObject("customfield_11709")
    def cfFechaFin = customFieldManager.getCustomFieldObject("customfield_11705")
    def cfUnit = customFieldManager.getCustomFieldObject("customfield_11701")
    def cfAgrupacionIT = customFieldManager.getCustomFieldObject("customfield_12711")
    def cfAreaIT = customFieldManager.getCustomFieldObject("customfield_12712")
    def cfCIO = customFieldManager.getCustomFieldObject("customfield_12223")
    def cfResponsableUnidad = customFieldManager.getCustomFieldObject("customfield_12604")
    def cfResponsableAgrupacionIT = customFieldManager.getCustomFieldObject("customfield_12713")
    //def cfResponsableAreaIT = customFieldManager.getCustomFieldObject("customfield_12714")
    def cfObjetivo = customFieldManager.getCustomFieldObject("customfield_11728")
    def cfObjetivoAct = customFieldManager.getCustomFieldObject("customfield_11775")
    def cfProjectManager = customFieldManager.getCustomFieldObject("customfield_10812")
    def cfITUnit = customFieldManager.getCustomFieldObject("customfield_11703")
    def fechaFinEsperadaSP = customFieldManager.getCustomFieldObject("customfield_12807")
    log.error("Test ${getNfeedValue(subproyecto, "customfield_11701")}")
    def unitITValueL = getNfeedValue(subproyecto, "customfield_11701").toLong() //unitIT
    log.error("Test ${unitITValueL}")
    def areaITValueL = getNfeedValue(subproyecto, "customfield_12712",).toLong() //areaIT
    def cfApplicacion = customFieldManager.getCustomFieldObject("customfield_11711") // nfeed
    //def agrupacionITValueL = getNfeedValue(subproyecto, "customfield_12711").toLong()//agrupacionIT
    def clienteValue = getNfeedValue(subproyecto, "customfield_16902")
    def costeEjecucionValue = getNfeedValue(subproyecto, "customfield_17102")
    //def responsableUnidadValue = subproyecto.getCustomFieldValue(cfResponsableUnidad)//userManager.getUserByName(getResponsableFromDB(unitITValueL))
    def responsableAgrupacionITValue = subproyecto.getCustomFieldValue(cfResponsableAgrupacionIT)//userManager.getUserByName(getUserByAgrupacion(agrupacionITValueL))
    //def responsableAreaITValue = subproyecto.getCustomFieldValue(cfResponsableAreaIT)//userManager.getUserByName(getResponsableFromDB(areaITValueL))
   
    //MET-1099
	def destinoID = 17100L
    destinoCF =  ComponentAccessor.customFieldManager.getCustomFieldObject(destinoID)
    def importeID = 17101L
    importeCF =  ComponentAccessor.customFieldManager.getCustomFieldObject(importeID)
	def destino = subproyecto.getCustomFieldValue(destinoCF)
    def importe = subproyecto.getCustomFieldValue(importeCF)
	destinoCF.updateValue(null,actividad, new ModifiedValue(null, destino),new DefaultIssueChangeHolder())
    importeCF.updateValue(null,actividad, new ModifiedValue(null, importe),new DefaultIssueChangeHolder())

    cfTipoActividad.updateValue(null, actividad, new ModifiedValue("", subproyecto.getCustomFieldValue(cfTipoActividad)),userDefaultIssue)
    cfFechaInicio.updateValue(null, actividad, new ModifiedValue("", subproyecto.getCustomFieldValue(cfFechaInicio)),userDefaultIssue)
    cfFechaFin.updateValue(null, actividad, new ModifiedValue("", subproyecto.getCustomFieldValue(fechaFinEsperadaSP)),userDefaultIssue)
    cfUnit.updateValue(null, actividad, new ModifiedValue("", subproyecto.getCustomFieldValue(cfUnit)), userDefaultIssue)
    cfITUnit.updateValue(null, actividad, new ModifiedValue("", subproyecto.getCustomFieldValue(cfITUnit)), userDefaultIssue)
    cfAgrupacionIT.updateValue(null, actividad, new ModifiedValue("", subproyecto.getCustomFieldValue(cfAgrupacionIT)), userDefaultIssue)
    cfAreaIT.updateValue(null, actividad, new ModifiedValue("", subproyecto.getCustomFieldValue(cfAreaIT)), userDefaultIssue)
    cfCIO.updateValue(null, actividad, new ModifiedValue("", subproyecto.getCustomFieldValue(cfCIO)), new DefaultIssueChangeHolder())
    cfApplicacion.updateValue(null, actividad, new ModifiedValue("", subproyecto.getCustomFieldValue(cfApplicacion)), userDefaultIssue)
    //cfResponsableUnidad.updateValue(null, actividad, new ModifiedValue("", [responsableUnidadValue]), userDefaultIssue)
    cfResponsableAgrupacionIT.updateValue(null, actividad, new ModifiedValue("", responsableAgrupacionITValue), userDefaultIssue)
    //cfResponsableAreaIT.updateValue(null, actividad, new ModifiedValue("", responsableAreaITValue), userDefaultIssue)
    cfProjectManager.updateValue(null, actividad, new ModifiedValue("", subproyecto.getCustomFieldValue(cfProjectManager)), userDefaultIssue)

    cfObjetivo.updateValue(null, actividad, new ModifiedValue("", subproyecto.getCustomFieldValue(cfObjetivo)), userDefaultIssue)
    cfObjetivoAct.updateValue(null, actividad, new ModifiedValue("", subproyecto.getCustomFieldValue(cfObjetivo)), userDefaultIssue)
    cfCodClarity.updateValue(null, actividad, new ModifiedValue("", subproyecto.getCustomFieldValue(cfCodClarity)), userDefaultIssue)
    cfResponsableUnidad.updateValue(null, actividad, new ModifiedValue(actividad.getCustomFieldValue(cfResponsableUnidad), [subproyecto.getCustomFieldValue(cfProjectManager)]),userDefaultIssue)
    setNfeedValue(actividad.getKey(),"customfield_16902", clienteValue)
    setNfeedValue(actividad.getKey(),"customfield_17102", costeEjecucionValue)

    //fechaFinEsperadaSP.updateValue(null, subproyecto, new ModifiedValue(subproyecto.getCustomFieldValue(fechaFinEsperadaSP), null),userDefaultIssue)
    if (subproyecto.getStatusId() == "10001"){
        def zSubproyectoCerrado = customFieldManager.getCustomFieldObject("customfield_13102")
        zSubproyectoCerrado.updateValue(null, actividad, new ModifiedValue(actividad.getCustomFieldValue(zSubproyectoCerrado), "1"),userDefaultIssue)
    }
}

def getResponsableFromDB(def value){
    def driver = Class.forName('com.microsoft.sqlserver.jdbc.SQLServerDriver').newInstance() as Driver
    def props = new Properties()
    props.setProperty("user", credentials.get("user"))
    props.setProperty("password", credentials.get("password"))
    def conn = driver.connect(credentials.get("connection"), props) //BBDD Maestra
    def sql = new Sql(conn)
    PreparedStatement statement = null

    try {
        String myStatement = "select UsuarioBS from usuario where id in (select ID_Usuario from ResponsableUnidad where ID_Unidad=?)";
        statement = conn.prepareStatement(myStatement);
        statement.setLong(1, value) //id
        ResultSet rs = statement.executeQuery()
        while (rs.next()) {
            def id = rs.getString(1)
            return id
        }
    } finally {
        sql.close()
        conn.close()
        statement.close()
    }
}

def getUserByAgrupacion(def value){
    def driver = Class.forName('com.microsoft.sqlserver.jdbc.SQLServerDriver').newInstance() as Driver
    def props = new Properties()
    props.setProperty("user", credentials.get("user"))
    props.setProperty("password", credentials.get("password"))
    def conn = driver.connect(credentials.get("connection"), props) //BBDD Maestra
    def sql = new Sql(conn)
    PreparedStatement statement = null

    try {
        String myStatement = "SELECT u.UsuarioBS FROM [BdHalleyMD].[dbo].[HALLEY_AGRUPACION] JOIN Usuario u ON u.ID=HALLEY_AGRUPACION.Responsable WHERE [HALLEY_AGRUPACION].ID = ?";
        statement = conn.prepareStatement(myStatement);
        statement.setLong(1, value) //id
        ResultSet rs = statement.executeQuery()
        while (rs.next()) {
            def id = rs.getString(1)
            return id
        }
    } finally {
        sql.close()
        conn.close()
        statement.close()
    }
}

def getDBCredentials(){
    def dbMap = [:]
    def connectionURL = customFieldManager.getCustomFieldObject("customfield_12626")
    def user = customFieldManager.getCustomFieldObject("customfield_12627")
    def password = customFieldManager.getCustomFieldObject("customfield_12628")
    dbMap.put("connection", issueConfig.getCustomFieldValue(connectionURL))
    dbMap.put("user", issueConfig.getCustomFieldValue(user))
    dbMap.put("password", issueConfig.getCustomFieldValue(password))
    return dbMap
}

def linkBcWithProject(def bc, def project){
    def linkType = issueLinkTypeManager.getIssueLinkType(new Long(10701))
    linkMgr.createIssueLink(project.getId(), bc.getId(), linkType.getId(), 1, userAdmin)
}

def setNfeedValue(String issueKey,String customFieldId, String nFeedKeyValue)
{
    def pluginAccessor = ComponentAccessor.getPluginAccessor();
    def plugin = pluginAccessor.getPlugin("com.valiantys.jira.plugins.SQLFeed");
    def serviceClass = plugin.getClassLoader().loadClass("com.valiantys.nfeed.api.IFieldValueService");
    def fieldValueService = ComponentAccessor.getOSGiComponentInstanceOfType(serviceClass);
    fieldValueService.setFieldValue(issueKey, customFieldId, nFeedKeyValue);
}