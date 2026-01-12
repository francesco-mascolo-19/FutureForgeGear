<%--
  Created by IntelliJ IDEA.
  User: luigi
  Date: 12/01/2026
  Time: 19:21
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home</title>
    <link rel="stylesheet" href="style/style.css"> <!
</head>
<body>

<%@include file="NavbarDedicata.jsp"%>


<section class="cover elemento">
    <div class="cover_filter"></div>
    <div class="cover_caption">
        <div class="cover_caption_copy">
            <h1>Welcome to Future Forge Gear</h1>


        </div>
    </div>
</section>





<section class="cards clearfix">
    <div class="card">
        <img src="image/pcfisso1.jpg" alt="" class="image">
        <div class="card_copy">
            <h3>Computer Preassemblati</h3>

        </div>

    </div>


    <div class="card">
        <img src="image/pcportatile1.jpg" alt="" class="image">
        <div class="card_copy">
            <h3>Computer Portatitili</h3>

        </div>

    </div>


    <div class="card">
        <img src="image/periferiche1.jpg" alt="" class="image">
        <div class="card_copy">
            <h3>Periferiche e componenti</h3>

        </div>

    </div>

</section>

<section class="banner clearfix">
    <div class="banner_image"></div>
    <div class="banner_copy">
        <div class="banner_copy_txt">

            <h4>Scegli il Computer Preassemblato perfetto per le tue esigenze</h4>
            <p>Ogni sistema è ottimizzato e configurato con cura, garantendo prestazioni, affidabilità e velocità per ogni tipo di utilizzo, dal gaming al lavoro professionale. </p>
        </div>
    </div>
</section>

<section class="banner clearfix">
    <div class="banner_image float banner_image2"></div>
    <div class="banner_copy float">
        <div class="banner_copy_txt">

            <h4 class="right">Scegli il Computer Portatile perfetto per la tua mobilità </h4>
            <p class="right">Ogni modello è progettato per offrire un equilibrio ideale tra potenza, leggerezza e autonomia, realizzato con materiali di alta qualità per garantire comfort, stile e produttività ovunque tu sia.</p>
        </div>
    </div>
</section>

<section class="banner clearfix">
    <div class="banner_image banner_image3"></div>
    <div class="banner_copy">
        <div class="banner_copy_txt">

            <h4>Scegli le Periferiche e i Componenti perfetti per il tuo setup</h4>
            <p>Ogni elemento è selezionato e testato per offrire affidabilità, compatibilità e funzionalità avanzate, creando un ambiente di lavoro o di gioco preciso e performante.</p>
        </div>
    </div>
</section>



<%@include file = "footer.jsp" %>
</body>
<script>
    $(document).ready(function(){
        $(".header_icon-bar").click(function(e){
            $(".header_menu").toggleClass('is-open');
            e.preventDefault();/*mettiamo questo codice in questo modo dato che la rimozone e l inerimento della classe is open avviene su un link dobbiamo far si che la pagina non venga ricaricata cosi da poter attivare l evento*/
        });

    });
</script>
</html>

