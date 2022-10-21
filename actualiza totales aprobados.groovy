//Calcula los totales aprobados de un Change Request

import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.CustomFieldManager
import groovy.transform.Field
import com.atlassian.jira.ComponentManager;
import Helper.CommonHelper
import Helper.BDHelper
import Helper.GridHelper
import Helper.NfeedHelper
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import java.text.DecimalFormat


@Field cfm = ComponentAccessor.getCustomFieldManager()
@Field GridHelper gh = new GridHelper()
@Field CommonHelper ch = new CommonHelper()
@Field NfeedHelper nfh = new NfeedHelper()
@Field BDHelper bdh = new BDHelper()

def issue = ComponentAccessor.issueManager.getIssueObject("PPR-11775")

//ch.reindexar(issue)


def gridModPEP = 18214 //GRID MODIFICACIÓN PEP EXISTENTES
def user = ch.getAdminUser()
def gridDataModPEP = gh.getAllGridRows(issue.id,gridModPEP,user)
//sleep(3000)

//VALORES APROBADOS//*******************
//TOTALES
def totalOpexApro_id =  16009//15115			//Total Opex Aprobado
def opexAñoActApr_id =   16010 //15116 			//Total Opex Año Actual Aprobado
def aprAñoAct_id = 12667				//Apr. año actual 
def totalApr_id = 12666					//Total Aprobado € K
def totalCapexApr_id = 16007			//Total Capex aprobado
def totalCapexAñoActualApr_id = 16008	//Total Capex Año actual aprobado

def totalOpexRecurrentes_id = 17707	//Total Recurrente
def totalOpexNoIT_id = 17705	//Total No IT



def cf_totalOpexApro    = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(totalOpexApro_id)
def cf_opexAñoActApr    = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(opexAñoActApr_id)
def cf_aprAñoAct    = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(aprAñoAct_id)
def cf_totalApr    = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(totalApr_id)
def cf_totalCapexApr    = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(totalCapexApr_id)
def cf_totalCapexAñoActualApr    = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(totalCapexAñoActualApr_id)

def cf_aprAñoAnt = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12720L); //MFS-3
def cf_totalOpexRecurrentes    = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(totalOpexRecurrentes_id)
def cf_totalOpexNoIT    = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(totalOpexNoIT_id)
//Seteo los aprobados ya que se calculan otra vez
def val_totalOpexApro = 0d
def val_opexAñoActApr = 0d
def val_aprAñoAct = 0d 
def val_totalApr = 0d
def val_totalCapexApr = 0d 
def val_totalCapexAñoActualApr = 0d

def val_aprAñoAnt = (issue.getCustomFieldValue(cf_aprAñoAnt) == null ) ? 0d : issue.getCustomFieldValue(cf_aprAñoAnt)//MFS-3
def val_totalOpexRecurrentes = (issue.getCustomFieldValue(cf_totalOpexRecurrentes) == null ) ? 0d : issue.getCustomFieldValue(cf_totalOpexRecurrentes) //MFS-3
def val_totalOpexNoIT = (issue.getCustomFieldValue(cf_totalOpexNoIT) == null) ? 0d : issue.getCustomFieldValue(val_totalOpexNoIT); //MFS-3
Long categoria_id						= 11500L;	//Categoría (campo perteneciente a los tickets PPR)
CustomFieldManager customFieldManager 	= ComponentAccessor.getCustomFieldManager();
CustomField categoria_cf				= customFieldManager.getCustomFieldObject(categoria_id);


gridDataModPEP.each(){
       def pep = it.tipopep
    	log.error(it.tipopep)
    	//Capex
       if(pep == 'HAR' || pep == 'LIC' || pep == 'SOF' || pep == 'ENP' || pep == 'OTR'){
    	val_totalCapexAñoActualApr = it.aprobadoY1+val_totalCapexAñoActualApr
		val_totalCapexApr = (it.aprobadoY1+it.aprobadoY2+it.aprobadoY3+it.aprobadoY4+it.aprobadoY5+val_totalCapexApr)
    	}
    	log.error("Total Capex "+ val_totalCapexApr)
    	//OPEX
   		if(pep == 'OPE' || pep == 'GGE' || pep == 'RAP' || pep == 'BUF'){
        val_opexAñoActApr = it.aprobadoY1+val_opexAñoActApr
		val_totalOpexApro = (it.aprobadoY1+it.aprobadoY2+it.aprobadoY3+it.aprobadoY4+it.aprobadoY5+val_totalOpexApro)
    }
}

val_aprAñoAct = val_totalCapexAñoActualApr+val_opexAñoActApr
//val_totalApr = val_totalCapexApr+val_totalOpexApro
val_totalApr = val_totalCapexApr+val_totalOpexApro+val_aprAñoAnt+val_totalOpexRecurrentes+val_totalOpexNoIT	//MFS-3
log.warn("val_totalApr "+val_totalApr)

//si es CR se calcula el Aprobado
if(isChangeRequest(issue.getCustomFieldValue(categoria_cf))){
    log.error("Total Capex "+ val_totalCapexApr)
    log.error("Total Opex "+ val_totalOpexApro)
    cf_totalOpexApro.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_totalOpexApro),val_totalOpexApro ),new DefaultIssueChangeHolder());
    cf_opexAñoActApr.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_opexAñoActApr),val_opexAñoActApr ),new DefaultIssueChangeHolder());
    cf_aprAñoAct.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_aprAñoAct),val_aprAñoAct ),new DefaultIssueChangeHolder());
    cf_totalApr.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_totalApr),val_totalApr ),new DefaultIssueChangeHolder());
    cf_totalCapexApr.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_totalCapexApr),val_totalCapexApr ),new DefaultIssueChangeHolder());
    cf_totalCapexAñoActualApr.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_totalCapexAñoActualApr),val_totalCapexAñoActualApr ),new DefaultIssueChangeHolder());

}

def isChangeRequest(def categoria){
    return categoria?.optionId == 10903L
}
