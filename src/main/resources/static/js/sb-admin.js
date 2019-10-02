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

    $(document).on('click', '#uploadbutton', function (event) {
        $("#fileup").click();
    });



    var r = new Resumable({
        target: '/api/upload/chunk',
        chunkSize: 1024*128,
        testChunks: false,
        simultaneousUploads: 3
    });

    r.assignBrowse(document.getElementById('fileup'));
    r.assignDrop(document.getElementById('content-wrapper'));

    r.on('fileAdded', function(file){
        file.relativePath = $("#fileTable").attr("directory");
        r.upload();
    });
    r.on('fileSuccess', function(file,message){
        filelistLoad($("#fileTable").attr("directory"));
        console.log("fileSuccess");
        console.log(file, message);
    });
    r.on('fileError', function(file, message){
        console.log("fileError");
        console.log(file, message);
    });

    function filelistLoad(directoryId) {
        let headers = {};
        headers[header] = token;
        if (directoryId === undefined) directoryId = "0";

        $("#fileTable").attr("directory", directoryId);

        $.ajax({
                type: 'post',
                url: '/api/filelist/directory/' + directoryId,
                headers: headers,
                success: function (data) {
                    $(document).off('click', 'tr.directoryitem');
                    $(document).off('click', 'tr.fileitem');
                    $("#fileTable tbody tr").remove();

                    data.content.forEach(function (file) {
                        let row;
                        if (file.directory) {
                            row = $("<tr itemid='" + file.id + "'  class=\"directoryitem\">")
                                .append('<td>' + file.filename + '</td>')
                                .append('<td>&nbsp;</td>')
                                .append('<td>&nbsp;</td>')
                                .append('<td>&nbsp;</td>')
                                .append('</tr>');
                        } else {
                            row = $("<tr itemid='" + file.id + "' class=\"fileitem\">>")
                                .append('<td>' + file.filename + '</td>')
                                .append('<td>' + file.dateModified + '</td>')
                                .append('<td>' + file.type + '</td>')
                                .append('<td>' + file.size + '</td>')
                                .append('</tr>');
                        }
                        $("#fileTable").append(row);
                        // Need to change Resumable query to current directory
                    });

                    $(document).on('click', 'tr.directoryitem', function (event) {
                        filelistLoad($(this).attr("itemid"));
                    });
                    $(document).on('click', 'tr.fileitem', function (event) {
                        window.location = '/file/download/' + $(this).attr("itemid");
                    });
                }
            }
        );
    }
    filelistLoad($("#fileTable").attr("directory"));

})(jQuery); // End of use strict
