<div class="header">
  <input type="hidden" id="msoId" value="${msoId}"/>
  <input type="hidden" id="msoType" value="${msoType}"/>
  <input type="hidden" id="msoName" value="${msoName}"/>
  <input type="hidden" id="locale" value="${locale}"/>
  <input type="hidden" id="piwik" value="${piwik}"/>
  <div class="floatL" id="mso_logo"><img alt="" src="${msoLogo}"/></div>
  <div id="setup_page" class="jqmWindow"></div>
  <div class="floatR">
    <p><spring:message code="cms.header.welcome"/> <span>${mso.contactEmail}</span></p>
    <a href="/9x9" class="sg" style="display:none"></a>
    <a href="javascript:" class="setup" id="setup"></a>
    <a href="${logoutUrl}" class="logout"></a>
  </div>
  <label class="lang" id="image_header_logout">${root}/images/cms/<spring:message code="cms.image.header_logout"/></label>
  <label class="lang" id="image_header_setup">${root}/images/cms/<spring:message code="cms.image.header_setup"/></label>
  <label class="lang" id="image_header_sg">${root}/images/cms/<spring:message code="cms.image.header_sg"/></label>
  <label class="lang" id="warning_please_wait"><spring:message code="cms.warning.please_wait"/></label>
</div>
