//PPR-12493
import com.atlassian.jira.component.ComponentAccessor
import Helper.CommonHelper
import Helper.NfeedHelper
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.Issue
import java.util.Collection;
import org.apache.log4j.Logger
import com.atlassian.jira.issue.customfields.option.Option
import com.atlassian.jira.issue.customfields.option.Options
import com.atlassian.jira.issue.customfields.manager.OptionsManager
import java.lang.Object
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.fields.CustomField
import groovy.transform.Field

CommonHelper ch = new CommonHelper()
NfeedHelper nfh = new NfeedHelper()

Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("PPR-11412")
//Issue issue = issue
def fastTrackCF =  ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_17105")
def fastTrack = issue.getCustomFieldValue(fastTrackCF)
def sasCF =  ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_17608")
def sistemasCF =  ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_17614")
def rfpCF =  ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_17616")
OptionsManager optionsManager                       = ComponentAccessor.getOptionsManager();

def preseleccion =  ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_17619")

def framework = nfh.nfeedDisplayValue(issue.key, "customfield_17625", true)
def plataforma = nfh.nfeedDisplayValue(issue.key, "customfield_17629",  true)
def capacidades = nfh.nfeedDisplayValue(issue.key, "customfield_17626",  true)
def infraestructura = nfh.nfeedDisplayValue(issue.key, "customfield_17627",  true)

def datos = nfh.nfeedDisplayValue(issue.key, "customfield_17628", true)
def customFieldManager = ComponentAccessor.getCustomFieldManager()
def sas =  (issue.getCustomFieldValue(sasCF) == null? 'No' : issue.getCustomFieldValue(sasCF))
def sistemas = (issue.getCustomFieldValue(sistemasCF) == null? 'No' :  issue.getCustomFieldValue(sistemasCF))
def rfp = (issue.getCustomFieldValue(rfpCF) == null? 'No' :  issue.getCustomFieldValue(rfpCF))
def array = ''
def arrayOption 
def trigerTCA = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_19504")
log.error('Nombre campo->'+trigerTCA)
//def fieldTCA = getFieldByName(trigerTCA)
//if(fastTrack == null)
//{
    def debeValidarse = "Este PPR debería validarse en el comité TCA ( Technical Configuration Authority)  previamente a su presentación en el comité de aprobación correspondiente. Por favor, indique en General Info > Scoring TCA la fecha de presentación una vez se haya presentado o bien el motivo por el cual se considera que no es necesaria su presentación."
    def Si = ComponentAccessor.getOptionsManager().getOptions(preseleccion.getConfigurationSchemes().listIterator().next().getOneAndOnlyConfig()).find{it.getValue() == 'Sí'}
    def No = ComponentAccessor.getOptionsManager().getOptions(preseleccion.getConfigurationSchemes().listIterator().next().getOneAndOnlyConfig()).find{it.getValue() == 'No'}

    def encontrado = false
    if(framework != null)
    {
        framework.each
        {
            if(it != 'Proteo 4' && it !='No Aplica')
            {
                encontrado = true
            }
        }
    }


    framework = encontrado

    //framework = (framework == null? false :   !framework.contains('Proteo 4') ||  !framework.contains('No Aplica') )
    plataforma = (plataforma  == null? false :  plataforma.toString().contains('Otros'))
    capacidades = (capacidades   == null? false :  capacidades.toString().contains('Otros')  )
    infraestructura =  (infraestructura == null? false :  infraestructura.toString().contains('Otros'))
    datos = (datos  == null? false :  datos.toString().contains('Otros'))

	if(framework) array+= 'Framework' // 15400 
	if(plataforma) array+= ' Plataforma' // 15401
	if(capacidades) array+= ' Capacidades' // 15402
	if(infraestructura) array+= ' Infraestructura' // 15403
	if(sas.value == 'Sí') array+= ' SAS' // 15404
	if(datos) array+= ' Datos' // 15405
	if(sistemas.value == 'Sí') array+= ' Sistemas' // 15406
	if(rfp.value == 'Sí') array+= ' RFP' // 15407

	log.error(">>>valor de framework: ${framework}")
    log.error(">>>valor de plataforma: ${plataforma}")
    log.error(">>>valor de capacidades: ${capacidades}")
    log.error(">>>valor de infraestructura: ${infraestructura}")
    log.error(">>>valor de sas.value: ${sas.value}")
    log.error(">>>valor de datos: ${datos}")
    log.error(">>>valor de sistemas.value: ${sistemas.value}")
    log.error(">>>valor de rfp.value: ${rfp.value}")
    log.error('Array->'+array)
    def arrayS = array.split(" ")
    log.error('Array S->'+arrayS)

	//log.error(">>>${issue.getCustomFieldValue(preseleccion)}")
    if(  framework   || plataforma  ||   capacidades  || infraestructura ||  sas.value == 'Sí' || datos  ||   sistemas.value == 'Sí' || rfp.value == 'Sí'	 )
    {
		Options options             = optionsManager.getOptions(trigerTCA.getRelevantConfig(issue)); 
        Option newOption1;Option newOption2;Option newOption3;Option newOption4;Option newOption5;Option newOption6;Option newOption7;Option newOption8;def newOption=[]
        //ch.message(debeValidarse,0)
        //preseleccion.updateValue(null,issue,new ModifiedValue(issue.getCustomFieldValue(preseleccion),Si),new DefaultIssueChangeHolder())
        Option newOption0 = options.getOptionForValue("None",null)
        if(framework)  newOption1 = options.getOptionForValue("Framework",null) // 15400 
        if(plataforma)  newOption2 = options.getOptionForValue("Plataforma",null) // 15401
        if(capacidades)  newOption3 = options.getOptionForValue("Capacidades",null) // 15402
        if(infraestructura)  newOption4 = options.getOptionForValue("Infraestructura",null) // 15403
        if(sas.value == 'Sí')  newOption5 = options.getOptionForValue("SAS",null)// 15404
        if(datos)  newOption6 = options.getOptionForValue("Datos",null) // 15405
        if(sistemas.value == 'Sí')  newOption7 = options.getOptionForValue("Sistemas",null) // 15406
        if(rfp.value == 'Sí')  newOption8 = options.getOptionForValue("RFP",null) // 15407
            Option newOptions
        //for(def x = 0; fx < arrayS.size(); x++){
            if(framework) newOption = newOption + [newOption1]
            if(plataforma) newOption = newOption + [newOption2] // 15401
            if(capacidades) newOption = newOption + [newOption3] // 15402
            if(infraestructura) newOption =newOption +  [newOption4] // 15403
            if(sas.value == 'Sí') newOption = newOption + [newOption5] // 15404
            if(datos) newOption = newOption + [newOption6] // 15405
            if(sistemas.value == 'Sí') newOption = newOption + [newOption7] // 15406
            if(rfp.value == 'Sí') newOption = newOption +  [newOption8] // 15407
			log.error('IN newOption->'+newOption)
            //log.error('ArrayS in->'+arrayS)
           // }
        //newOption = [newOption1] + [newOption5]
        
			log.error('OUT newOption->'+newOptions)
        	log.error('array->'+issue.getCustomFieldValue(trigerTCA))
			
        	//log.error('New option->'+newOption)
        trigerTCA.updateValue(null, issue, new ModifiedValue(issue.getCustomFieldValue(trigerTCA), newOption), new DefaultIssueChangeHolder());
        		log.error(">>>>>Cumple TCA")
    }else
    {

        //preseleccion.updateValue(null,issue,new ModifiedValue(issue.getCustomFieldValue(preseleccion),No),new DefaultIssueChangeHolder())
        log.error(">>>>>NO Cumple TCA")
    }

    log.error(">>>${issue.getCustomFieldValue(preseleccion)}")




 
//}

