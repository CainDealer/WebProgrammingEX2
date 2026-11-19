(function () {
  'use strict';

  function feedbackOf(input) {
    var box = input.closest('.mb-3, .form-check');
    return box ? box.querySelector('.invalid-feedback') : null;
  }

  function customRules(input) {
    input.setCustomValidity('');
    var msg = '';

    if (input.dataset.match) {
      var other = input.form.elements[input.dataset.match];
      if (other && input.value !== other.value) {
        msg = input.dataset.msgMatch || 'Giá trị nhập lại không khớp.';
      }
    }
    if (!msg && input.type === 'file' && input.files && input.files.length) {
      var f = input.files[0];
      var exts = (input.dataset.ext || '').split(',').filter(Boolean);
      var ext = f.name.split('.').pop().toLowerCase();
      if (exts.length && exts.indexOf(ext) < 0) {
        msg = 'Định dạng không hợp lệ. Chỉ chấp nhận: ' + exts.join(', ') + '.';
      } else if (input.dataset.maxMb && f.size > input.dataset.maxMb * 1024 * 1024) {
        msg = 'File quá lớn (tối đa ' + input.dataset.maxMb + ' MB).';
      }
    }
    if (msg) input.setCustomValidity(msg);
  }

  function refresh(input) {
    customRules(input);
    var ok = input.checkValidity();
    input.classList.toggle('is-invalid', !ok);
    input.classList.toggle('is-valid', ok && input.value !== '');
    var fb = feedbackOf(input);
    if (!ok && fb) {
      fb.textContent = input.validity.customError
        ? input.validationMessage
        : (input.dataset.msg || input.validationMessage);
    }
    return ok;
  }

  document.querySelectorAll('form.needs-validation').forEach(function (form) {
    var controls = Array.prototype.filter.call(
      form.querySelectorAll('input, textarea, select'),
      function (c) { return c.type !== 'hidden' && c.type !== 'checkbox'; });

    controls.forEach(function (c) {
      var evt = c.type === 'file' ? 'change' : 'input';
      c.addEventListener('blur', function () { c.dataset.touched = '1'; refresh(c); });
      c.addEventListener(evt, function () {
        if (c.type === 'file') c.dataset.touched = '1';
        if (c.dataset.touched) refresh(c);
        var dependent = form.querySelector('[data-match="' + c.name + '"]');
        if (dependent && dependent.dataset.touched) refresh(dependent);
      });
    });

    form.addEventListener('submit', function (e) {
      var firstBad = null;
      controls.forEach(function (c) {
        c.dataset.touched = '1';
        if (!refresh(c) && !firstBad) firstBad = c;
      });
      if (firstBad) {
        e.preventDefault();
        e.stopPropagation();
        firstBad.focus();
        return;
      }
      form.querySelectorAll('button[type=submit]').forEach(function (b) {
        setTimeout(function () { b.disabled = true; }, 0);
      });
    });
  });
})();