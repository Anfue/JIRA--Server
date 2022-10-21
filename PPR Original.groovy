import com.atlassian.jira.ComponentManager
import com.atlassian.jira.component.ComponentAccessor

LinkedHashMap.metaClass.multiPut << { key, value ->
    delegate[key] = delegate[key] ?: []; delegate[key] += value
}

//import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.util.PathUtils
import com.atlassian.plugin.PluginAccessor
import groovy.sql.Sql
import groovy.transform.Field
import Helper.GridHelper
import Helper.WSHelper
import Helper.NfeedHelper
import Helper.CommonHelper
import java.sql.*
import java.sql.Driver
import java.sql.Array
import oracle.sql.ArrayDescriptor
import org.apache.log4j.Logger
import org.apache.log4j.Level

log.error("SCRIPT Cargar datos BC original")
currentUser = ComponentAccessor.jiraAuthenticationContext.loggedInUser
userAdmin = ComponentAccessor.userManager.getUserByName("ajira")
ComponentAccessor.jiraAuthenticationContext.setLoggedInUser(userAdmin)
@Field GridHelper gh =  new GridHelper()
@Field CommonHelper  ch =  new CommonHelper()
@Field NfeedHelper   nfh =  new NfeedHelper()
@Field WSHelper  wsh =  new WSHelper()


issueManager = ComponentAccessor.getIssueManager()
customFieldManager = ComponentAccessor.getCustomFieldManager()
optionsManager = ComponentAccessor.optionsManager
issue = ComponentAccessor.getIssueManager().getIssueObject("PPR-11017")

issueBC = issue
actividades = issueBC.getSubTaskObjects()
log.error("Actividades: " + actividades)



@Field cfm = ComponentAccessor.getCustomFieldManager()
@Field user = ch.getAdminUser()
@Field noitgridid = 18117
@Field recurrentesgridid = 17104
@Field noitgridexistentesid = 18212
@Field recurrentesgridexistentesid = 18213
@Field pepgridexistentesid = 18214
@Field pepid = 18211


@Field double totalYear1 = 0d
@Field double totalYear2 = 0d
@Field double totalYear3 = 0d
@Field double totalYear4 = 0d
@Field double totalYear5 = 0d
@Field double prior = 0d
	
//NFeed plugin
pluginAccessor = ComponentAccessor.getPluginAccessor();
def plugin = pluginAccessor.getPlugin("com.valiantys.jira.plugins.SQLFeed");
def serviceClass = plugin.getClassLoader().loadClass("com.valiantys.nfeed.api.IFieldValueService");
fieldValueService = ComponentAccessor.getOSGiComponentInstanceOfType(serviceClass);

//Table Grid plugin
Class dataManagerClass = pluginAccessor.getClassLoader().findClass("com.idalko.jira.plugins.igrid.api.data.TGEGridTableDataManager");
tgeGridDataManager = ComponentAccessor.getOSGiComponentInstanceOfType(dataManagerClass);

//CAMPOS

@Field def cfPepId = ComponentAccessor.customFieldManager.getCustomFieldObject(pepid)


//Objeto Demanda
demanda = null
long LINKDEMID = 10300L;
List<IssueLink> LinksBC =  ComponentAccessor.getIssueLinkManager().getInwardLinks(issueBC.id)
for(IssueLink Link : LinksBC){
    if (Link.getIssueLinkType().getId().equals(LINKDEMID)){
        demanda = Link.getSourceObject();
        break
    }
}

log.error("Objecto DEMANDA: " + demanda)



//usuario
user = ComponentAccessor.getJiraAuthenticationContext().getUser().getDirectoryUser()
issueConfig = issueManager.getIssueObject("CH-2")


//Objeto Proyecto
def cfSelecProyecto = customFieldManager.getCustomFieldObject('customfield_11507')
def proyectoID = getNfeedValue(demanda, "customfield_11507") //cfSelecProyecto
proyecto = issueManager.getIssueObject(Long.valueOf(proyectoID))
log.error("Objecto Proyecto: " + proyecto)
subproyectos = proyecto.getSubTaskObjects()
log.error("Subproyectos: " + subproyectos)

//BC Original
BCOriginal = obtenerBC(proyecto)
def obtenerBC(def proyecto) {
    List<IssueLink> LinksPROY = ComponentAccessor.getIssueLinkManager().getOutwardLinks(proyecto.id)
    
    for(def c=0; c < LinksPROY.size(); c++){
        //log.warn(LinksPROY[c].issueLinkType.id)
        if (LinksPROY[c].issueLinkType.id == 10701){
            log.warn("PRUEBA: "+LinksPROY[c].id)
            return LinksPROY[c].getDestinationObject()
        }
    }

}
log.error("Objecto BC Original: " + BCOriginal)

