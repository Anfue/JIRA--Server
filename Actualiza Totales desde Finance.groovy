//issue.getCustomFieldValue(customFieldManager.getCustomFieldObject(11500L)).toString().equals("Proyecto corporativo")
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

def issue = ComponentAccessor.issueManager.getIssueObject("PPR-7915")
/*
CAPEX: HAR,LIC,SOF, ENP y OTR
OPEX:  OPE, GGE, RAP, BUF
*/

def gridID = 18120 // GRID PEP 
def gridIDnoIt = 18117 // Costes NO IT
boolean comprobacion = true
def user = ch.getAdminUser()
def cf_TotalSol = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12659L )
def cf_TotalCapexSol = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16003L )
def cf_TotalOpexSol = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16005L )
def cf_TotalOpexNoItSol = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17702L)
def cf_TotalOpexRecSolicitado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17700L )

def val_TotalSol = issue.getCustomFieldValue(cf_TotalSol)
def val_TotalCapexSol = issue.getCustomFieldValue(cf_TotalCapexSol)
def val_TotalOpexSol = issue.getCustomFieldValue(cf_TotalOpexSol)
def val_TotalOpexNoItSol = (issue.getCustomFieldValue(cf_TotalOpexNoItSol)== null) ? 0 : issue.getCustomFieldValue(cf_TotalOpexNoItSol)
def val_TotalOpexRecSolicitado = (issue.getCustomFieldValue(cf_TotalOpexRecSolicitado) == null) ? 0 : issue.getCustomFieldValue(cf_TotalOpexRecSolicitado)

def gridData = gh.getAllGridRows(issue.id,gridID,user)
def gridDataNoIt = gh.getAllGridRows(issue.id,gridIDnoIt,user)

gridData.each()//por cada Linea de Coste del Grid One Time
{
//log.warn('1.-'+it)
def sumanos = 0
def pep = it.tipopep.name
log.error('Tipo pep -->'+it.tipopep.name)
    if(pep == 'HAR' || pep == 'LIC' || pep == 'SOF' || pep == 'ENP' || pep == 'OTR'){
        log.error('Suma valores en Capex')
        sumanos = it.y1+it.y2+it.y3
        cf_TotalCapexSol.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_TotalCapexSol),val_TotalCapexSol+sumanos),new DefaultIssueChangeHolder());
    	val_TotalCapexSol = issue.getCustomFieldValue(cf_TotalCapexSol)
    }
    if(pep == 'OPE' || pep == 'GGE' || pep == 'RAP' || pep == 'BUF'){
        log.error('Suma valores en OPEX')
        sumanos = it.y1+it.y2+it.y3
        cf_TotalOpexSol.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_TotalOpexSol),val_TotalCapexSol+sumanos),new DefaultIssueChangeHolder());
        val_TotalOpexSol = issue.getCustomFieldValue(cf_TotalOpexSol)
    }
}

def valoresTotales = val_TotalCapexSol+val_TotalOpexSol+val_TotalOpexNoItSol+val_TotalOpexRecSolicitado
log.error('Total Solicitado ='+valoresTotales)
cf_TotalSol.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_TotalSol), valoresTotales),new DefaultIssueChangeHolder());



