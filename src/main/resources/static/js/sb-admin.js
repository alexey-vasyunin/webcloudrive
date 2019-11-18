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

    // Upload button
    $(document).on('click', '#uploadbutton', function (event) {
        $("#fileup").click();
    });

    // New directory button
    $(document).on('submit', '#newdirform', function (event) {
        event.preventDefault();
        let headers = {};
        headers[header] = token;

        $.ajax({
            type: 'post',
            url: '/api/directory',
            headers: headers,
            data: {
                id: $("#fileTable").attr("directory"),
                name: $("#dirname").val()
            },
            success: function () {
                filelistLoad($("#fileTable").attr("directory"));
            }
        })
    });



    var r = new Resumable({
        target: '/api/file',
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
    });

    function filelistLoad(directoryId) {
        let headers = {};
        headers[header] = token;
        if (directoryId === undefined) directoryId = "0";

        $("#fileTable").attr("directory", directoryId);

        $.ajax({
                type: 'get',
                url: '/api/directory/' + directoryId,
                headers: headers,
                success: function (data) {
                    $(document).off('click', 'td.directoryitem');
                    $("#fileTable tbody tr").remove();

                    data.content.forEach(function (file) {
                        console.log(file);
                        let row;
                        if (file.directory) {
                            row = $("<tr>")
                                .append('<td  class="directoryitem" itemid="' + file.id + '">' + file.filename + '</td>')
                                .append((file.filename !== '..') ? '<td><div class="dropdown">\n' +
                                    '  <button class="btn btn-light btn-sm" type="button" id="dropdownMenuDirButton' + file.id + '" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>\n' +
                                    '  <div class="dropdown-menu" aria-labelledby="dropdownMenuDirButton' + file.id + ' ">\n' +
                                    '    <a class="dropdown-item rename-dir-button" href="#" data-itemid="' + file.id + '" data-itemtype="directory" id="renameDirButton' + file.id + '" data-toggle="modal" data-target="#renameModal" data-whatever="' + file.filename + '">Переименовать</a>\n' +
                                    '    <a class="dropdown-item delete-dir-button" href="#" id="deleteDirButton' + file.id + '">Удалить</a>\n' +
                                    '  </div>\n' +
                                    '</div></td>' : '&nbsp;')
                                .append('<td>&nbsp;</td>')
                                .append('<td>&nbsp;</td>')
                                .append('<td>&nbsp;</td>')
                                .append('</tr>');
                        } else {
                            row = $("<tr itemid='" + file.id + "' class=\"fileitem\">")
                                .append('<td><a href="/api/file/' + file.id + '"  tabindex="0" itemid="' + file.id + '" class="text-muted" ' + ((file.preview.filenames.length > 0)? ' data-toggle="popover" data-trigger="hover" data-content="***********************" data-container="body" ' : '') + '>' + file.filename + '</a></td>')
                                .append('<td><div class="dropdown">\n' +
                                    '  <button class="btn btn-light btn-sm" type="button" id="dropdownMenuButton' + file.id + '" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>\n' +
                                    '  <div class="dropdown-menu" aria-labelledby="dropdownMenuButton' + file.id + ' ">\n' +
                                    '    <a class="dropdown-item download-button" href="#" id="downloadButton' + file.id + '">Скачать</a>\n' +
                                    '    <a class="dropdown-item rename-button" href="#" data-itemid="' + file.id + '"  data-itemtype="file" id="renameButton' + file.id + '" data-toggle="modal" data-target="#renameModal" data-whatever="' + file.filename + '">Переименовать</a>\n' +
                                    '    <a class="dropdown-item delete-button" href="#" id="deleteButton' + file.id + '">Удалить</a>\n' +
                                    '  </div>\n' +
                                    '</div></td>')
                                .append('<td>' + file.dateModified + '</td>')
                                .append('<td>' + file.type + '</td>')
                                .append('<td>' + file.size + '</td>')
                                .append('</tr>');
                        }
                        $("#fileTable").append(row);
                        // Need to change Resumable query to current directory
                    });

                    $(document).on('click', 'td.directoryitem', function (event) {
                        filelistLoad($(this).attr("itemid"));
                    });

                    $(document).on('click', 'a.download-button', function (event) {
                        let id = event.target.id.substring('downloadButton'.length);
                        window.location = '/api/file/' + id;
                    });
                    $(document).on('click', 'a.delete-button', function (event) {
                        let id = event.target.id.substring('deleteButton'.length);
                        console.log(id);
                        $.ajax({
                            method: "DELETE",
                            url: "/api/file",
                            data: { id: id }
                        }).done(function( msg ) {
                                filelistLoad($("#fileTable").attr("directory"));
                        });
                    });

                    $(document).on('click', 'a.delete-dir-button', function (event) {
                        let id = event.target.id.substring('deleteDirButton'.length);
                        $.ajax({
                            method: "DELETE",
                            url: "/api/directory",
                            data: { id: id }
                        }).done(function( msg ) {
                            filelistLoad($("#fileTable").attr("directory"));
                        });
                    });

                    // $(".preview").each(function(a,b,c){ console.log($(b).attr('itemid')) });

                }
            }
        );
    }
    filelistLoad($("#fileTable").attr("directory"));

    $('#renameModal').on('show.bs.modal', function (event) {
        let button = $(event.relatedTarget); // Button that triggered the modal
        let recipient = button.data('whatever'); // Extract info from data-* attributes
        let id = button.data('itemid'); // Extract info from data-* attributes
        let type = button.data('itemtype'); // Extract info from data-* attributes
        let modal = $(this);
        modal.find('.modal-body input#new-name').val(recipient);
        modal.find('.modal-body input#id').val(id);
        modal.find('.modal-body input#type').val(type);
    });

    $('#saveRenameButton').on('click', function (event) {
        let name = $('.modal-body input#new-name').val();
        let id = $('.modal-body input#id').val();
        let type = $('.modal-body input#type').val();
        let url = '';
        switch (type) {
            case "file": url = '/api/file'; break;
            case "directory": url = '/api/directory'; break;
        }

        let headers = {};
        headers[header] = token;

        $.ajax({
            type: 'put',
            url: url,
            headers: headers,
            data: {
                id: id,
                name: name
            },
            success: function (data) {
                filelistLoad($("#fileTable").attr("directory"));
            }
        });

    });



})(jQuery); // End of use strict
