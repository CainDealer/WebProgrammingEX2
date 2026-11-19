<%@ tag body-content="empty" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ attribute name="name" required="true"%>
<%@ attribute name="label" required="true"%>
<%@ attribute name="type"%>
<%@ attribute name="value"%>
<%@ attribute name="error"%>
<%@ attribute name="msg"%>
<%@ attribute name="pattern"%>
<%@ attribute name="minlength"%>
<%@ attribute name="maxlength"%>
<%@ attribute name="placeholder"%>
<%@ attribute name="autocomplete"%>
<%@ attribute name="match"%>
<%@ attribute name="required" type="java.lang.Boolean"%>
<div class="mb-3">
  <label for="f-${name}" class="form-label"><c:out value="${label}"/><c:if test="${required}"> <span class="text-danger">*</span></c:if></label>
  <input id="f-${name}" name="${name}" type="${empty type ? 'text' : type}"
         class="form-control${not empty error ? ' is-invalid' : ''}"
         ${required ? 'required' : ''}
         <c:if test="${type ne 'password'}">value="<c:out value='${value}'/>"</c:if>
         <c:if test="${not empty pattern}">pattern="<c:out value='${pattern}'/>"</c:if>
         <c:if test="${not empty minlength}">minlength="${minlength}"</c:if>
         <c:if test="${not empty maxlength}">maxlength="${maxlength}"</c:if>
         <c:if test="${not empty placeholder}">placeholder="<c:out value='${placeholder}'/>"</c:if>
         <c:if test="${not empty autocomplete}">autocomplete="${autocomplete}"</c:if>
         <c:if test="${not empty match}">data-match="${match}"</c:if>
         <c:if test="${not empty msg}">data-msg="<c:out value='${msg}'/>"</c:if>>
  <div class="invalid-feedback"><c:out value="${not empty error ? error : msg}"/></div>
</div>