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

@Field cfm = ComponentAccessor.getCustomFieldManager()
@Field GridHelper gh = new GridHelper()
@Field CommonHelper ch = new CommonHelper()
@Field NfeedHelper nfh = new NfeedHelper()
@Field BDHelper bdh = new BDHelper()

def issue = ComponentAccessor.issueManager.getIssueObject("PPR-9020")
def gridID = 18120 // GRID PEP 
def gridIDnoIt = 18117 // Costes NO IT
boolean comprobacion = false
def user = ch.getAdminUser()
def cf_TotalApr = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12666L )
def cf_TotalSol = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12659L )
def cf_TotalOpexAnoActualSol = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16006L)
def cf_TotalOpexSol = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16005L)
def cf_TotalOpexNoItSol = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17702L)
def val_TotalApr = issue.getCustomFieldValue(cf_TotalApr)
def val_TotalSol = issue.getCustomFieldValue(cf_TotalSol)
def val_TotalOpexAnoActualSol = issue.getCustomFieldValue(cf_TotalOpexAnoActualSol)
def val_TotalOpexSol = issue.getCustomFieldValue(cf_TotalOpexSol)
def val_TotalOpexNoItSol = issue.getCustomFieldValue(cf_TotalOpexNoItSol)
log.error('val_TotalOpexAnoActualSol-->'+val_TotalOpexAnoActualSol)
log.error('val_TotalOpexSol-->'+val_TotalOpexSol)
def gridData = gh.getAllGridRows(issue.id,gridID,user)
def gridDataNoIt = gh.getAllGridRows(issue.id,gridIDnoIt,user)
log.error(val_TotalApr)
log.error(val_TotalSol)
def suma = (val_TotalApr+val_TotalSol)
log.warn('Suma='+suma)
gridData.each()//por cada Linea de Coste del Grid One Time
{
log.warn('1.-'+it)
log.error( 'Cuenta GIC-->'+it.gic)
log.error('Campo PD-->'+it.pd.name)
log.error('Tipo pep-->'+it.tipopep.name)
def sumanos = it.y1+it.y2+it.y3
log.error('Suma años-->'+sumanos)
    if (it.pd.name == 'OPE' && it.gic != null && it.pd.name == 'No' && val_TotalOpexAnoActualSol > 150000){
        comprobacion = true
    }
    if(it.pd.name == 'OPE' && it.gic != null && it.pd.name == 'Si' && val_TotalOpexSol > 500000 ){
        comprobacion = true
    }
}
gridDataNoIt.each()//por cada Linea de Coste del Grid One Time

{
    
log.warn('2-'+it)
    log.warn('Planned-->'+it.planned.name)
    if(it.planned.name == 'No'){
        comprobacion = true
    }
    if(it.planned.name == 'Si' && val_TotalOpexNoItSol > 500000){
        comprobacion = true
    }
}

if (suma > 2000000) comprobacion = true

return comprobacion

