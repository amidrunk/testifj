var index = (function ($) {
    $("li.nav-item > a").each(function(index, element) {
        var href = element.href;
        var location = href.substring(href.lastIndexOf('#') + 1);

        Navigation.on(actionThat(isEqualTo(location))).then(function() {
            $("li.active").removeClass("active");
            $(element).parent().addClass("active");
            $("#contentPanel").load(location + ".html");
        });
    });

    return {
    }
})(jQuery);