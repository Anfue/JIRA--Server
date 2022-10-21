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

def issue = ComponentAccessor.issueManager.getIssueObject("PPR-9236")

def gridID = 18221 // ID tabla One time
def user = ch.getAdminUser()
def gridData = gh.getAllGridRows(issue.id,gridID,user)

def cf_TotalSol = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12659L ) // está
def cf_TotalOpexAnoSol = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16006L )
def cf_TotalOpexRecSolicitado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17700L )
def cf_TotalCapexSolicitado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16003L) //está
def cf_TotalOpexSol = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16005L) //está
def cf_TotalOpexNoItSol = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17702L) // está
def SolAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12660L )
def CapexSolAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16004L )
def OpexNoItSolAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17703L )
def OpexRecuSolAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17701L )
def OpexSolAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16006L )
def cf_TotalAprobado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12666L ) // está
def cf_TotalOpexRecAprobado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17707L )
def cf_TotalCapexAprobado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16007L) //está
def cf_TotalOpexAprobado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16009L) //está
def cf_TotalOpexNoItAprobado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17705L) // está
def AprAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12667L )
def CapexAprAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16008L )
def OpexNoItAprAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17704L )
def OpexRecuAprAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17706L )
def OpexAprAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16010L )

def val_TotalSol = issue.getCustomFieldValue(cf_TotalSol)
def val_TotalOpexAnoSol = (issue.getCustomFieldValue(cf_TotalOpexAnoSol) == null) ? 0 : issue.getCustomFieldValue(cf_TotalOpexAnoSol)
def val_TotalOpexRecSolicitado = (issue.getCustomFieldValue(cf_TotalOpexRecSolicitado) == null) ? 0 : issue.getCustomFieldValue(cf_TotalOpexRecSolicitado)
def val_TotalCapexSolicitado = (issue.getCustomFieldValue(cf_TotalCapexSolicitado) == null) ? 0 : issue.getCustomFieldValue(cf_TotalCapexSolicitado)
def val_TotalOpexSol = (issue.getCustomFieldValue(cf_TotalOpexSol) == null) ? 0 : issue.getCustomFieldValue(cf_TotalOpexSol)
def val_TotalOpexNoItSol = (issue.getCustomFieldValue(cf_TotalOpexNoItSol)== null) ? 0 : issue.getCustomFieldValue(cf_TotalOpexNoItSol)
def val_SolAnoActual = issue.getCustomFieldValue(SolAnoActual)
def val_CapexSolAnoActual = (issue.getCustomFieldValue(CapexSolAnoActual)== null) ? 0 : issue.getCustomFieldValue(CapexSolAnoActual)
def val_OpexNoItSolAnoActual = (issue.getCustomFieldValue(OpexNoItSolAnoActual)== null) ? 0 : issue.getCustomFieldValue(OpexNoItSolAnoActual)
def val_OpexRecuSolAnoActual = (issue.getCustomFieldValue(OpexRecuSolAnoActual)== null) ? 0 : issue.getCustomFieldValue(OpexRecuSolAnoActual)
def val_OpexSolAnoActual = (issue.getCustomFieldValue(OpexSolAnoActual)== null) ? 0 : issue.getCustomFieldValue(OpexSolAnoActual)
def val_TotalApr = issue.getCustomFieldValue(cf_TotalAprobado)
def val_TotalOpexRecAprobado = (issue.getCustomFieldValue(cf_TotalOpexRecAprobado) == null) ? 0 : issue.getCustomFieldValue(cf_TotalOpexRecAprobado)
def val_TotalCapexAprobado = (issue.getCustomFieldValue(cf_TotalCapexAprobado) == null) ? 0 : issue.getCustomFieldValue(cf_TotalCapexAprobado)
def val_TotalOpexAprobado = (issue.getCustomFieldValue(cf_TotalOpexAprobado) == null) ? 0 : issue.getCustomFieldValue(cf_TotalOpexAprobado)
def val_TotalOpexNoItAprobado = (issue.getCustomFieldValue(cf_TotalOpexNoItAprobado)== null) ? 0 : issue.getCustomFieldValue(cf_TotalOpexNoItAprobado)
def val_AprAnoActual= issue.getCustomFieldValue(AprAnoActual)
def val_CapexAprAnoActual = (issue.getCustomFieldValue(CapexAprAnoActual)== null) ? 0 : issue.getCustomFieldValue(CapexAprAnoActual)
def val_OpexNoItAprAnoActual = (issue.getCustomFieldValue(OpexNoItAprAnoActual)== null) ? 0 : issue.getCustomFieldValue(OpexNoItAprAnoActual)
def val_OpexRecuAprAnoActual = (issue.getCustomFieldValue(OpexRecuAprAnoActual)== null) ? 0 : issue.getCustomFieldValue(OpexRecuAprAnoActual)
def val_OpexAprAnoActual= (issue.getCustomFieldValue(OpexAprAnoActual)== null) ? 0 : issue.getCustomFieldValue(OpexAprAnoActual)
def val_TotalOpexAnoO = 0
def val_TotalOpexAnoC = 0

gridData.each()//por cada Linea de Coste del Grid One Time
{
    log.warn('1--'+it)
    def pep = it.tipopep.name
	log.error('Tipo pep -->'+it.tipopep.name)
    if(pep == 'HAR' || pep == 'LIC' || pep == 'SOF' || pep == 'ENP' || pep == 'OTR'){
        log.error('Suma valores en Capex')
    	val_TotalOpexAnoC = it.y1+val_TotalOpexAnoC
    }
    if(pep == 'OPE' || pep == 'GGE' || pep == 'RAP' || pep == 'BUF'){
        log.error('Suma valores en OPEX')
        val_TotalOpexAnoO = it.y1+val_TotalOpexAnoO
    }

}
cf_TotalOpexAnoSol.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_TotalOpexAnoSol),val_TotalOpexAnoO ),new DefaultIssueChangeHolder());
def sumaTotales = (val_TotalOpexRecSolicitado+val_TotalCapexSolicitado+val_TotalOpexSol+val_TotalOpexNoItSol)
def sumaActuales = (val_CapexSolAnoActual+val_OpexNoItSolAnoActual+val_OpexRecuSolAnoActual+val_OpexSolAnoActual)
def sumaTotalesA = (val_TotalOpexRecAprobado+val_TotalCapexAprobado+val_TotalOpexAprobado+val_TotalOpexNoItAprobado)
def sumaActualesA = (val_CapexAprAnoActual+val_OpexNoItAprAnoActual+val_OpexRecuAprAnoActual+val_OpexAprAnoActual)
/*log.warn ('Suma Totales Solicitados-->'+sumaTotales)
log.error('Ver sumaTotales Sol-->'+val_TotalOpexRecSolicitado+' + '+val_TotalCapexSolicitado+' + '+val_TotalOpexSol+' + '+val_TotalOpexNoItSol)
log.warn ('Suma Actuales Sol-->'+sumaActuales)
log.error('Ver sumaActuales Sol-->'+val_CapexSolAnoActual+' + '+val_OpexNoItSolAnoActual+' + '+val_OpexRecuSolAnoActual+' + '+val_OpexSolAnoActual)
log.warn ('Suma Totales Aprobados-->'+sumaTotalesA)
log.error('Ver sumaTotalesA Apr-->'+val_TotalOpexRecAprobado+' + '+val_TotalCapexAprobado+' + '+val_TotalOpexAprobado+' + '+val_TotalOpexNoItAprobado)
log.warn ('Suma Actuales Apr-->'+sumaActualesA)
log.error('Ver sumaActuales  Apr-->'+val_CapexAprAnoActual+' + '+val_OpexNoItAprAnoActual+' + '+val_OpexRecuAprAnoActual+' + '+val_OpexAprAnoActual)
*/
//MAIN
// Hay que sumar Total Opex Solicitado + Total OPEX recurrente solicitado + Total OPEX no IT solicitado = Total Solicitado
log.error('val_TotalSol('+val_TotalSol+') < sumaTotales('+sumaTotales+')')
if (val_TotalSol <= sumaTotales){
    log.error('Entra Totales')
cf_TotalSol.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_TotalSol),sumaTotales ),new DefaultIssueChangeHolder());
}
log.error('val_SolAnoActual('+val_SolAnoActual+') < sumaActuales('+sumaActuales+')')
if (val_SolAnoActual <= sumaActuales){
    log.error('Entra Actuales')
SolAnoActual.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(SolAnoActual),sumaActuales ),new DefaultIssueChangeHolder());
}
log.error('val_TotalSol('+val_TotalSol+') < sumaTotales('+sumaTotalesA+')')
if (val_TotalSol <= sumaTotalesA){
    log.error('Entra Totales')
cf_TotalAprobado.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_TotalAprobado),sumaTotalesA ),new DefaultIssueChangeHolder());
}
log.error('val_SolAnoActual('+val_AprAnoActual+') < sumaActualesA('+sumaActualesA+')')
if (val_AprAnoActual <= sumaActualesA){
    log.error('Entra Actuales')
AprAnoActual.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(AprAnoActual),sumaActualesA ),new DefaultIssueChangeHolder());
}
