import com.atlassian.jira.component.ComponentAccessor
import java.text.SimpleDateFormat 
import java.util.Date
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.Issue


Date now = new Date();
Calendar calendar = Calendar.getInstance();
calendar.setTime(now);

/************Acceso issue CONSOLA*******************/
def issue = ComponentAccessor.getIssueManager().getIssueObject("PPR-11848")
/************Acceso issue CONSOLA*******************/
int yearNow = calendar.get(Calendar.YEAR)
double ano = 0
def cf_clasificar =ComponentAccessor.getCustomFieldManager().getCustomFieldObject(17500L)
def val_clasificar = issue.getCustomFieldValue(cf_clasificar).toString()
def cf_valorAnoActual =ComponentAccessor.getCustomFieldManager().getCustomFieldObject(12706L)
def val_anoActual = issue.getCustomFieldValue(cf_valorAnoActual)
def cfCategoria = ComponentAccessor.getCustomFieldManager().getCustomFieldObject(11500L)
def categoriaValue = issue.getCustomFieldValue(cfCategoria).optionId
log.error('categoriaValue-->' + val_anoActual)
if (categoriaValue != 10903L) {
    return 'No es Change Request'
}
if (val_clasificar.contains('actual')){
    ano = yearNow
}
if (val_clasificar.contains('siguiente')){
    ano = yearNow+1
}
ano = ano.toInteger()
log.error('Año-->'+ano)
cf_valorAnoActual.updateValue(null,issue, new ModifiedValue(issue.getCustomFieldValue(cf_valorAnoActual),ano ),new DefaultIssueChangeHolder());

return ano