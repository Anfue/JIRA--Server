import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.user.ApplicationUser
import groovy.sql.Sql

import java.sql.Driver;

def issueManager = ComponentAccessor.issueManager

def issue = issueManager.getIssueObject("PPR-8769")


def pluginAccessor = ComponentAccessor.getPluginAccessor()
def customFieldManager = ComponentAccessor.customFieldManager
def plugin = pluginAccessor.getPlugin("com.valiantys.jira.plugins.SQLFeed")
def serviceClass = plugin.getClassLoader().loadClass("com.valiantys.nfeed.api.IFieldValueService")
fieldValueService = ComponentAccessor.getOSGiComponentInstanceOfType(serviceClass)

def issueConfig = issueManager.getIssueObject("CH-2")
//def credentialsHalley = getDBCredentialsHalley(customFieldManager, issueConfig)
//def credentialsClarity = getDBCredentialsClarity(customFieldManager, issueConfig)
//def propertiesHalley = new Properties()
//propertiesHalley.setProperty("user", credentialsHalley.get("user"))
//propertiesHalley.setProperty("password", credentialsHalley.get("password"))
//def propertiesClarity = new Properties()
//propertiesClarity.setProperty("user", credentialsClarity.get("user"))
//propertiesClarity.setProperty("password", credentialsClarity.get("password"))

def TOTALID = 12659
def Y1ID = 12660
def Y2ID = 12661
def Y3ID = 12662
def Y4ID = 12663
def Y5ID = 12664
//Pestaña aprobado
def aprAñoAnt_id = 12720  				//Apr. años anter. € k
def totalOpexApro_id =  16009//15115			//Total Opex Aprobado
def opexAñoActApr_id =   16010 //15116 			//Total Opex Año Actual Aprobado
def aprAñoAct_id = 12667				//Apr. año actual 
def totalApr_id = 12666					//Total Aprobado € K
def totalCapexApr_id = 16007			//Total Capex aprobado
def totalCapexAñoActualApr_id = 16008	//Total Capex Año actual aprobado

def totalOpexRecurrenteApr_id = 17707  //MFS
def totalOpexRecurrenteActApr_id = 17706 //MFS
def totalOpexNoItApr_id = 17705 //MFS
def totalOpexNoitActApr_id = 17704 //MFS
 


CustomField cfAgrupacionITDemanda = customFieldManager.getCustomFieldObject(14925L)
CustomField cfUnitGD = customFieldManager.getCustomFieldObject(14924L)


//BEGIN MFS
def  totalOpexRecurrenteApr =  customFieldManager.getCustomFieldObject(totalOpexRecurrenteApr_id)
def  totalOpexRecurrenteActApr =  customFieldManager.getCustomFieldObject(totalOpexRecurrenteActApr_id)
def  totalOpexNoItApr  =  customFieldManager.getCustomFieldObject(totalOpexNoItApr_id)
def  totalOpexNoitActApr  =  customFieldManager.getCustomFieldObject(totalOpexNoitActApr_id)
//END MFS

def cf_total = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(TOTALID)
def cf_y1 = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(Y1ID)
def cf_y2 = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(Y2ID)
def cf_y3 = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(Y3ID)
def cf_y4 = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(Y4ID)
def cf_y5 = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(Y5ID)

//ACEX-6351
def cf_o1 = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17700)
def cf_o2 = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17701)
def cf_o3= ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17702)
def cf_o4 = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17703)

//Pestaña aprobado
def cf_aprAñoAnt    = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(aprAñoAnt_id)
def cf_totalOpexApro    = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(totalOpexApro_id)
def cf_opexAñoActApr    = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(opexAñoActApr_id)
def cf_aprAñoAct    = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(aprAñoAct_id)
def cf_totalApr    = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(totalApr_id)
def cf_totalCapexApr    = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(totalCapexApr_id)
def cf_totalCapexAñoActualApr    = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(totalCapexAñoActualApr_id)

cf_total.updateValue(null,issue, new ModifiedValue(null,0d),new DefaultIssueChangeHolder());
cf_y1.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());
cf_y2.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());
cf_y3.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());
cf_y4.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());
cf_y5.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());

//ACEX-6351
cf_o1.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());
cf_o2.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());
cf_o3.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());
cf_o4.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());




//Pestaña aprobado
totalOpexRecurrenteApr.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());     //MFS
totalOpexRecurrenteActApr.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());//MFS
totalOpexNoItApr.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());//MFS
totalOpexNoitActApr.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());//MFS

cf_aprAñoAnt.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());
//cf_totalOpexApro.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());
//cf_opexAñoActApr.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());
//cf_aprAñoAct.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());
//cf_totalApr.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());
//cf_totalCapexApr.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());
cf_totalCapexAñoActualApr.updateValue(null,issue, new ModifiedValue(null, 0d),new DefaultIssueChangeHolder());