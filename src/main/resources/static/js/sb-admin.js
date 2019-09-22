(function ($) {
    "use strict"; // Start of use strict

    var token = $("meta[name='_csrf']").attr("content");
    var header = $("meta[name='_csrf_header']").attr("content");

    // Toggle the side navigation
    $("#sidebarToggle").on('click', function (e) {
        e.preventDefault();
        $("body").toggleClass("sidebar-toggled");
        $(".sidebar").toggleClass("toggled");
    });

    // Prevent the content wrapper from scrolling when the fixed side navigation hovered over
    $('body.fixed-nav .sidebar').on('mousewheel DOMMouseScroll wheel', function (e) {
        if ($(window).width() > 768) {
            var e0 = e.originalEvent,
                delta = e0.wheelDelta || -e0.detail;
            this.scrollTop += (delta < 0 ? 1 : -1) * 30;
            e.preventDefault();
        }
    });

    // Scroll to top button appear
    $(document).on('scroll', function () {
        var scrollDistance = $(this).scrollTop();
        if (scrollDistance > 100) {
            $('.scroll-to-top').fadeIn();
        } else {
            $('.scroll-to-top').fadeOut();
        }
    });

    // Smooth scrolling using jQuery easing
    $(document).on('click', 'a.scroll-to-top', function (event) {
        var $anchor = $(this);
        $('html, body').stop().animate({
            scrollTop: ($($anchor.attr('href')).offset().top)
        }, 1000, 'easeInOutExpo');
        event.preventDefault();
    });

    // let row = $("<tr>");
    // row
    //     .append("")


    let headers = {};
    headers[header] = token;
    $.ajax({
            type: 'post',
            url: '/api/filelist',
            headers: headers,
            success: function (data) {
                data.forEach(function (file) {
                    let row = $("<tr fileid='" + file.id + "'>")
                        .append('<td>' + file.filename + '</td>')
                        .append('<td>' + file.dateModified + '</td>')
                        .append('<td>' + file.type + '</td>')
                        .append('<td>' + file.size + '</td>')
                        .append('</tr>');
                    $("#fileTable").append(row);
                });
            }
        }
    );

})(jQuery); // End of use strict
