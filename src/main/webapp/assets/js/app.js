document.addEventListener('DOMContentLoaded', function () {
  document.querySelectorAll('[data-confirm]').forEach(function (el) {
    el.addEventListener('click', function (event) {
      if (!window.confirm(el.dataset.confirm)) event.preventDefault();
    });
  });

  document.querySelectorAll('.alert[data-auto-dismiss]').forEach(function (el) {
    setTimeout(function () {
      if (window.bootstrap) bootstrap.Alert.getOrCreateInstance(el).close();
    }, 4500);
  });

  var toggle = document.querySelector('[data-sidebar-toggle]');
  var sidebar = document.querySelector('.shell-sidebar');
  if (toggle && sidebar) {
    toggle.addEventListener('click', function () { sidebar.classList.toggle('open'); });
    sidebar.querySelectorAll('a').forEach(function (link) {
      link.addEventListener('click', function () { sidebar.classList.remove('open'); });
    });
    document.addEventListener('keydown', function (event) {
      if (event.key === 'Escape') sidebar.classList.remove('open');
    });
  }
});
