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
import com.atlassian.jira.issue.index.IssueIndexingService

//Constantes y managers
@Field cfm = ComponentAccessor.getCustomFieldManager()
@Field GridHelper gh = new GridHelper()
@Field CommonHelper ch = new CommonHelper()
@Field NfeedHelper nfh = new NfeedHelper()
@Field BDHelper bdh = new BDHelper()

/************Acceso issue CONSOLA*******************/
def issue = ComponentAccessor.getIssueManager().getIssueObject("PPR-12305")
//IDs campos
def gridID = 18221L //  Verificacion LC finance
def gridIDPEP = 18120L //  tabla PEP
def gridIDMod = 18214L // Modificacion PEP existente
//usuario de accion
def user = ch.getAdminUser()
log.error('USER-->' + user.class)
def pluginAccessor = ComponentAccessor.getPluginAccessor();
changeSetClass = pluginAccessor.getClassLoader().findClass("com.idalko.jira.plugins.igrid.api.data.dto.TGEChangeSet");
Class dataManagerClass = pluginAccessor.getClassLoader().findClass("com.idalko.jira.plugins.igrid.api.data.TGEGridTableDataManager");
def tgeGridDataManager = ComponentAccessor.getOSGiComponentInstanceOfType(dataManagerClass);
List changes = new ArrayList()
def rows = tgeGridDataManager.readGridData(issue.getId(), gridID, null, null, 0, 100, user);
def gridData = gh.getAllGridRows(issue.id, gridID, user)
def gridDataPEP = gh.getAllGridRows(issue.id, gridIDPEP, user)
def gridDataMod = gh.getAllGridRows(issue.id, gridIDMod, user)
double sumaSoly = 0.0
double ValEnp = 0.0
def Id 
def valueS1=0d;def valueS2=0d;def valueS3=0d;def valueS4=0d;def valueS5=0d;
def value1 = 0d;def value2 = 0d;def value3 = 0d;def value4 = 0d;def value5 = 0d
def VENPOr1 = 0d;def VENPOr2 = 0d;def VENPOr3 = 0d;def VENPOr4 = 0d;def VENPOr5 = 0d
def ID_ENP
def ID_SOF
def cliente; def sscte; def articulos; def prov; def nivelsup; def pdName;
def Id_ENP = ['']
def Apr_ENP1 = [0.0];def Apr_ENP2 = [0.0];def Apr_ENP3 = [0.0];def Apr_ENP4 = [0.0];def Apr_ENP5 = [0.0]
def TotAprENP = 0
def TotAprENPCalculado = 0
def grid = 0
def Ids_ENP = ['']
def xxx = 0
def xxxx = 0
def eventoENP
def conSOF = 0
def sol1 = 0d;
def sol2 = 0d;
def sol3 = 0d;
def sol4 = 0d;
def sol5 = 0d;
def valSOF
def valENPs 
def xx = 0
def xtrue = false
def act = ['']
def sscte = ['']
def unidadit = ['']
gridDataMod.each()
{
	if ( it.tipopep == 'ENP'){
	grid++
	Apr_ENP1[grid] = it.aprobadoY1;Apr_ENP2[grid] = it.aprobadoY2;Apr_ENP3[grid] = it.aprobadoY3;Apr_ENP4[grid] = it.aprobadoY4; Apr_ENP5[grid] = it.aprobadoY5
	Ids_ENP[grid] = it.nompep
	act[grid] = it.actividad
	sscte[grid] = it.sscte
	unidadit[grid] = it.unidadit
	}
}
log.error(Ids_ENP)
grid = 0
gridDataMod.each()
{
    //log.error(it)
if (it.tipopep == 'ENP'){ 
grid++
 TotAprENP = it.aprobadoY2
 Ids_ENP[grid]
    log.error('-->'+Ids_ENP[grid])
	gridDataMod.each()
	{
		if (it.tipopep == 'ENP' && Ids_ENP[grid]){
		//******/
		gridDataPEP.each() //por cada Linea de Verificacion LC finance
			{	
				//log.error('it.tipopep.name-->'+it)
				Ids_ENP[grid]
				//log.warn('Tipo de pep='+it.tipopep.name+',id='+it.IDpep +', it.y2-->'+ it.y2+',Apr_ENP2[grid]-->'+Apr_ENP2[grid])
				xtrue = false
				if(unidadit[grid] == it.unidadit && sscte[grid] == it.sscte && it.tipopep.name == 'ENP' && (Apr_ENP2[grid] == it.y2 || Apr_ENP1[grid] == it.y1 ) && act[grid] == it.nombreactividad)  {
					//log.warn(' it.y1-->'+ it.y1);log.error('value1-->'+value1);log.error('VENPOr1-->'+VENPOr1)
					//log.error('En PEP de SOF('+Id+') tiene como ENP el '+it.IDpep +' y se pone el valor año 1 de '+value1)
					Map < String, Object > row = new HashMap < String, Object > ();
					row.put("IDpep", Ids_ENP[grid])
					log.error('Se modifica el ENP existente en tabla PEP ')
					xtrue = true
					gh.updateRowGrid(issue.getId(), gridIDPEP, user, row, it.id)
				}
			}
			gridData.each() //por cada Linea de Verificacion LC finance
			{	
				//log.error('it.tipopep.name-->'+it.tipopep.name)
				if(it.tipopep.name == 'ENP' && xtrue == true){
					//log.warn(' it.y1-->'+ it.y1);log.error('value1-->'+value1);log.error('VENPOr1-->'+VENPOr1)
					//log.error('En PEP de SOF('+Id+') tiene como ENP el '+it.IDpep +' y se pone el valor año 1 de '+value1)
					Map < String, Object > row = new HashMap < String, Object > ();
					row.put("IDpep", Ids_ENP[grid])
					log.error('Se modifica el ENP existente en tabla VF')
					gh.updateRowGrid(issue.getId(), gridID, user, row, it.id)
				}
		}
		/*****/
		}
	}
}
}
