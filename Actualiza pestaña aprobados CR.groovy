/*********************************************/
/* Script Mostrar agregado PEP en HTML       */
/* Autor: Jfernafu                           */
/*********************************************/
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue
import groovy.transform.Field
import com.atlassian.jira.ComponentManager;
import Helper.CommonHelper
import Helper.GridHelper
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.ModifiedValue

//Constantes y managers
def cfm = ComponentAccessor.getCustomFieldManager()

@Field GridHelper gh =  new GridHelper()
@Field CommonHelper ch =  new CommonHelper()

def issue = ComponentAccessor.getIssueManager().getIssueObject("PPR-11574") 

//IDs campos
def categoriaid = 11500
def gridIDCR = 18213L // 17401
def gridIDNIT = 18212L
def cfcategoria = ComponentAccessor.customFieldManager.getCustomFieldObject(categoriaid)
def categoria = issue.getCustomFieldValue(cfcategoria).optionId 

def cf_TotalOpexRecAprobado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17707L )
def cf_TotalOpexRecAnoActualAprobado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17706L )
def cf_TotalOpexNITAprobado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17705L )
def cf_TotalOpexNITAnoActualAprobado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17704L )
def cf_TotalAprobado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12666L )
def cf_TotalAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12667L )
def cf_TotalAnosAnteriores = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12720L )
def cf_Total_Opex_aprobado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject (16009L)
def cf_TotalOpex_ano_actual_aprobado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject (16010L)

def val_Total_Opex_aprobado = issue.getCustomFieldValue(cf_Total_Opex_aprobado)
def val_TotalOpex_ano_actual_aprobado = issue.getCustomFieldValue(cf_TotalOpex_ano_actual_aprobado)
def totalRec = 0d
def totRecano = 0d
def totalNIT = 0d
def totalNITano = 0d
def suma_total = 0d
def suma_anual = 0d

log.warn('Categoria-->'+categoria)

//usuario de accion
def user = ch.getAdminUser() 
  
def gridDataCR = gh.getAllGridRows(issue.id,gridIDCR,user)
def gridDataNIT = gh.getAllGridRows(issue.id,gridIDNIT,user)
log.warn('Xx-->'+gridDataCR)

gridDataCR.each()//por cada Linea de Coste Recurrentes
{
   log.error('R-'+it)
    totalRec = totalRec + it.aprobadoY1 + it.aprobadoY3 + it.aprobadoY3 + it.aprobadoY4 + it.aprobadoY5
    totRecano = totRecano + it.aprobadoY1
}

gridDataNIT.each()//por cada Linea de Coste NO IT
{
   log.warn('N-'+it)
    totalNIT = totalNIT + it.aprobadoY1 + it.aprobadoY3 + it.aprobadoY3 + it.aprobadoY4 + it.aprobadoY5
    totalNITano = totalNITano + it.aprobadoY1
}
log.error('T Recurrentes-->'+totalRec+' y anuales-->'+totRecano)
log.error('T NIT-->'+totalNIT+' y anuales-->'+totalNITano)

suma_total = val_Total_Opex_aprobado+totalRec+totalNIT
suma_anual = totalNITano+totRecano+val_TotalOpex_ano_actual_aprobado

cf_TotalOpexRecAprobado.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_TotalOpexRecAprobado),totalRec ),new DefaultIssueChangeHolder());
cf_TotalOpexRecAnoActualAprobado.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_TotalOpexRecAnoActualAprobado),totRecano ),new DefaultIssueChangeHolder());
cf_TotalOpexNITAprobado.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_TotalOpexNITAprobado),totalNIT ),new DefaultIssueChangeHolder());
cf_TotalOpexNITAnoActualAprobado.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_TotalOpexNITAnoActualAprobado),totalNITano ),new DefaultIssueChangeHolder());
cf_TotalAprobado.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_TotalAprobado),suma_total ),new DefaultIssueChangeHolder());
cf_TotalAnoActual.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_TotalAnoActual),suma_anual ),new DefaultIssueChangeHolder());
cf_TotalAnosAnteriores.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_TotalAnosAnteriores),suma_total-suma_anual ),new DefaultIssueChangeHolder());
/*
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

//def issue = ComponentAccessor.issueManager.getIssueObject("PPR-11396")

//ch.reindexar(issue)

def gridIDRec = 18213 //GRID Costes Recurrentes Existentes
def gridIDNoIT = 18212 //GRID Costes No IT Existentes

def user = ch.getAdminUser()
def gridDataRec = gh.getAllGridRows(issue.id,gridIDRec,user)
def gridDataNoIT = gh.getAllGridRows(issue.id,gridIDNoIT,user)

Long categoria_id						= 11500L;	//Categoría (campo perteneciente a los tickets PPR)
CustomFieldManager customFieldManager 	= ComponentAccessor.getCustomFieldManager();
CustomField categoria_cf				= customFieldManager.getCustomFieldObject(categoria_id);

//sleep(3000)
//VALORES APROBADOS//*******************
//TOTALES
def cf_TotalApr = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12666L ) 
def cf_TotalAprAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12667L )


//Recogemos los aprobados que ya hay en la pestaña, todos menos Recurrentes y NO IT
def val_TotalApr = issue.getCustomFieldValue(cf_TotalApr)
def val_TotalAprAnoActual = issue.getCustomFieldValue(cf_TotalAprAnoActual)

def TotalAprAnoActualNoIT = 0.0d
def TotalAprobadoNoIT = 0.0d
def TotalAprAnoActualRec = 0.0d
def TotalAprobadoRec = 0.0d


gridDataRec.each()//por cada Linea de Coste Recurrente Existentes
{
	TotalAprAnoActualRec = TotalAprAnoActualRec+it.aprobadoY1
	TotalAprobadoRec = (it.aprobadoY1+it.aprobadoY2+it.aprobadoY3+it.aprobadoY4+it.aprobadoY5+TotalAprobadoRec)    
}

gridDataNoIT.each()//por cada Linea de Coste No IT Existentes
{
	TotalAprAnoActualNoIT = TotalAprAnoActualNoIT+it.aprobadoY1
	TotalAprobadoNoIT = (it.aprobadoY1+it.aprobadoY2+it.aprobadoY3+it.aprobadoY4+it.aprobadoY5+TotalAprobadoNoIT)     
}
//A los totales, se añaden los NO IT y Recurrentes
val_TotalAprAnoActual = val_TotalAprAnoActual + TotalAprAnoActualRec + TotalAprAnoActualNoIT
val_TotalApr = val_TotalApr + TotalAprobadoNoIT + TotalAprobadoRec

if(isChangeRequest(issue.getCustomFieldValue(categoria_cf))){
	cf_TotalApr.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_TotalApr),val_TotalApr ),new DefaultIssueChangeHolder());
	cf_TotalAprAnoActual.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_TotalAprAnoActual),val_TotalAprAnoActual),new DefaultIssueChangeHolder());
}
*/