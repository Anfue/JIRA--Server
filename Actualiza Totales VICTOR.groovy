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

def issue = ComponentAccessor.issueManager.getIssueObject("PPR-9313")

def gridID = 18221 // ID tabla Verificacion Finance
def user = ch.getAdminUser()
def gridData = gh.getAllGridRows(issue.id,gridID,user)
sleep(3000)
//VALORES SOLICITADOS//*******************
//TOTALES
def cf_TotalSol = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12659L ) 
def cf_TotalSolAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12660L )//está
def cf_TotalOpexAnoSol = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16006L )
def cf_TotalOpexRecSolicitado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17700L )
def cf_TotalCapexSolicitado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16003L) 
def cf_TotalOpexSol = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16005L) 
def cf_TotalOpexNoItSol = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17702L) 

def val_TotalSol = issue.getCustomFieldValue(cf_TotalSol)
def val_TotalSolAnoActual = issue.getCustomFieldValue(cf_TotalSolAnoActual)
def val_TotalOpexAnoSol = (issue.getCustomFieldValue(cf_TotalOpexAnoSol) == null) ? 0d : issue.getCustomFieldValue(cf_TotalOpexAnoSol)
def val_TotalOpexRecSolicitado = (issue.getCustomFieldValue(cf_TotalOpexRecSolicitado) == null) ? 0d : issue.getCustomFieldValue(cf_TotalOpexRecSolicitado)
def val_TotalCapexSolicitado = (issue.getCustomFieldValue(cf_TotalCapexSolicitado) == null) ? 0d : issue.getCustomFieldValue(cf_TotalCapexSolicitado)
def val_TotalOpexSol = (issue.getCustomFieldValue(cf_TotalOpexSol) == null) ? 0d : issue.getCustomFieldValue(cf_TotalOpexSol)
def val_TotalOpexNoItSol = (issue.getCustomFieldValue(cf_TotalOpexNoItSol)== null) ? 0d : issue.getCustomFieldValue(cf_TotalOpexNoItSol)

//ACTUALES
def cf_SolAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12660L )//está
def cf_CapexSolAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16004L )
def cf_OpexNoItSolAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17703L )
def cf_OpexRecuSolAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17701L )
def cf_OpexSolAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16006L )//----///

def val_SolAnoActual = issue.getCustomFieldValue(cf_SolAnoActual)
def val_CapexSolAnoActual = (issue.getCustomFieldValue(cf_CapexSolAnoActual)== null) ? 0d : issue.getCustomFieldValue(cf_CapexSolAnoActual)
def val_OpexNoItSolAnoActual = (issue.getCustomFieldValue(cf_OpexNoItSolAnoActual)== null) ? 0d : issue.getCustomFieldValue(cf_OpexNoItSolAnoActual)
def val_OpexRecuSolAnoActual = (issue.getCustomFieldValue(cf_OpexRecuSolAnoActual)== null) ? 0d : issue.getCustomFieldValue(cf_OpexRecuSolAnoActual)
def val_OpexSolAnoActual = (issue.getCustomFieldValue(cf_OpexSolAnoActual)== null) ? 0d : issue.getCustomFieldValue(cf_OpexSolAnoActual)

//VALORES APROBADOS//**********
//TOTALES
def cf_TotalAprobado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12666L ) // está
def cf_AprAnoAnter = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12720L ) // está
def cf_TotalOpexRecAprobado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17707L )
def cf_TotalCapexAprobado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16007L) //está
def cf_TotalOpexAprobado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16009L) //está
def cf_TotalOpexNoItAprobado = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17705L) // está

def val_TotalApr = issue.getCustomFieldValue(cf_TotalAprobado)
def val_AprAnoAnter = issue.getCustomFieldValue(cf_AprAnoAnter)
def val_TotalOpexRecAprobado = (issue.getCustomFieldValue(cf_TotalOpexRecAprobado) == null) ? 0d : issue.getCustomFieldValue(cf_TotalOpexRecAprobado)
def val_TotalCapexAprobado = (issue.getCustomFieldValue(cf_TotalCapexAprobado) == null) ? 0d : issue.getCustomFieldValue(cf_TotalCapexAprobado)
def val_TotalOpexAprobado = (issue.getCustomFieldValue(cf_TotalOpexAprobado) == null) ? 0d : issue.getCustomFieldValue(cf_TotalOpexAprobado)
def val_TotalOpexNoItAprobado = (issue.getCustomFieldValue(cf_TotalOpexNoItAprobado)== null) ? 0d : issue.getCustomFieldValue(cf_TotalOpexNoItAprobado)

//ACTUALES
def cf_AprAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12667L )
def cf_CapexAprAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16008L )
def cf_OpexNoItAprAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17704L )
def cf_OpexRecuAprAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17706L )
def cf_OpexAprAnoActual = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(16010L )

def val_AprAnoActual= issue.getCustomFieldValue(cf_AprAnoActual)
def val_CapexAprAnoActual = (issue.getCustomFieldValue(cf_CapexAprAnoActual)== null) ? 0d : issue.getCustomFieldValue(cf_CapexAprAnoActual)
def val_OpexNoItAprAnoActual = (issue.getCustomFieldValue(cf_OpexNoItAprAnoActual)== null) ? 0d : issue.getCustomFieldValue(cf_OpexNoItAprAnoActual)
def val_OpexRecuAprAnoActual = (issue.getCustomFieldValue(cf_OpexRecuAprAnoActual)== null) ? 0d : issue.getCustomFieldValue(cf_OpexRecuAprAnoActual)
def val_OpexAprAnoActual= (issue.getCustomFieldValue(cf_OpexAprAnoActual)== null) ? 0d : issue.getCustomFieldValue(cf_OpexAprAnoActual)
def sumActuales = (val_CapexAprAnoActual+val_OpexNoItAprAnoActual+val_OpexRecuAprAnoActual+val_OpexAprAnoActual)
//Variables didtinast para trabajar
def val_TotalOpexAnoO = 0d
def val_TotalOpexAnoC = 0d
def val_TotalOpexO = 0d
def val_TotalOpexC = 0d
def xxx = (val_OpexRecuSolAnoActual+val_OpexNoItSolAnoActual)

//RECORREMOS TABLEGRID

def val_TotalOpexAno = 0d
def val_TotalCapexAno = 0d
def val_TotalOpex = 0d
def val_TotalCapex = 0d

gridData.each()//por cada Linea de Coste del Grid One Time
{
    log.warn('1--'+it)
    def pep = it.tipopep.name
	log.error('Tipo pep -->'+it.tipopep.name)
    if(pep == 'HAR' || pep == 'LIC' || pep == 'SOF' || pep == 'ENP' || pep == 'OTR'){
        log.error('Suma valores en Capex = '+it.y1)
    	val_TotalCapexAno = it.y1+val_TotalCapexAno
		val_TotalCapex = (it.y1+it.y2+it.y3+it.y4+it.y5+val_TotalCapex)
        log.error('Suma Capex='+it.y1+'+'+it.y2+'+'+it.y3+'+'+it.y4+'+'+it.y5+'+'+val_TotalCapex+'='+val_TotalCapex)
    }
    if(pep == 'OPE' || pep == 'GGE' || pep == 'RAP' || pep == 'BUF'){
        log.error('Suma valores en OPEX = '+it.y1)
        val_TotalOpexAno = it.y1+val_TotalOpexAno
		val_TotalOpex = (it.y1+it.y2+it.y3+it.y4+it.y5+val_TotalOpex)
        log.error('Suma Opex='+it.y1+'+'+it.y2+'+'+it.y3+'+'+it.y4+'+'+it.y5+'+'+val_TotalOpex+'='+val_TotalOpex)

    }
}
log.error('val_TotalCapexAno-->'+val_TotalCapexAno)
log.error('val_TotalCapex-->'+val_TotalCapex)
log.error('val_TotalOpexAno-->'+val_TotalOpexAno)
log.error('Total OPEX solicitado-->'+val_TotalOpex)


/// DEFINIMOS SUMAS FINALES
def sumaTotalesSolicitados = (val_TotalCapexSolicitado+val_TotalOpexSol+val_TotalOpexRecSolicitado+val_TotalOpexNoItSol)
def sumaActualesSolicitados = (val_TotalOpexAnoC+val_TotalOpexAnoO+val_OpexNoItSolAnoActual+val_OpexRecuSolAnoActual)
def sumaTotalesAprobados = (val_TotalOpexRecAprobado+val_TotalCapexAprobado+val_TotalOpexAprobado+val_TotalOpexNoItAprobado)
def sumaActualesAprobados = (val_CapexAprAnoActual+val_OpexNoItAprAnoActual+val_OpexRecuAprAnoActual+val_OpexAprAnoActual)
log.error('val_TotalCapexAprobado-->'+val_TotalCapexAprobado)
//MAIN
log.error('val_TotalSol('+val_TotalSol+') < sumaTotalesSolicitados('+sumaTotalesSolicitados+')')
if (val_TotalSol < sumaTotalesSolicitados){
    log.error('Entra Totales SOl')
//cf_TotalSol.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_TotalSol),sumaTotalesSolicitados ),new DefaultIssueChangeHolder());
}
log.error('val_SolAnoActual('+val_SolAnoActual+') < sumaActualesSolicitados('+sumaActualesSolicitados+')')
//if (val_SolAnoActual < sumaActualesSolicitados){
    log.error('Entra Actuales Sol')
cf_SolAnoActual.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_SolAnoActual),val_TotalCapexAno ),new DefaultIssueChangeHolder());
//}

log.error('val_TotalApr('+val_TotalApr+') < sumaTotales('+sumaTotalesAprobados+')')
if (val_TotalApr < sumaTotalesAprobados){
    log.error('Entra Totales Apr')
cf_TotalAprobado.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_TotalAprobado),sumaTotalesAprobados ),new DefaultIssueChangeHolder());
}/*
log.error('val_SolAnoActual('+val_AprAnoActual+') < sumaActualesAprobados('+sumaActualesAprobados+')')
if (val_AprAnoActual < sumaActualesAprobados){
    log.error('Entra Actuales Apr')
AprAnoActual.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(AprAnoActual),sumaActualesAprobados ),new DefaultIssueChangeHolder());
}
def anteriores = (val_TotalApr-sumActuales)
log.error('anteriores-->'+anteriores)
cf_AprAnoAnter.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_AprAnoAnter),null ),new DefaultIssueChangeHolder());
*/

