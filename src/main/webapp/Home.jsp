<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>FutureForgeGear</title>
    <link rel="stylesheet" href="style/style.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
        }

        .container {
            max-width: 1200px;
            margin: 100px auto 50px;
            padding: 20px;
        }

        .welcome-section {
            text-align: center;
            margin-bottom: 50px;
        }

        .welcome-section h1 {
            color: #2c3e50;
            font-size: 36px;
            margin-bottom: 10px;
        }

        .welcome-section p {
            color: #7f8c8d;
            font-size: 18px;
        }

        .categories {
            display: flex;
            justify-content: space-around;
            flex-wrap: wrap;
            gap: 20px;
            margin-bottom: 50px;
        }

        .category-card {
            background: white;
            border-radius: 8px;
            padding: 20px;
            width: 250px;
            text-align: center;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            transition: transform 0.3s;
        }

        .category-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
        }

        .category-card img {
            width: 200px;
            height: 150px;
            object-fit: cover;
            border-radius: 4px;
            margin-bottom: 15px;
        }

        .category-card h3 {
            color: #3498db;
            margin-bottom: 10px;
        }

        .product-showcase {
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }

        .product-showcase h2 {
            color: #2c3e50;
            text-align: center;
            margin-bottom: 20px;
        }

        .features {
            display: flex;
            justify-content: space-around;
            flex-wrap: wrap;
            gap: 20px;
        }

        .feature {
            text-align: center;
            width: 200px;
        }

        .feature h4 {
            color: #3498db;
            margin: 10px 0;
        }
    </style>
</head>
<body>

<%@include file = "Navbar.jsp" %>

<div class="container">
    <div class="welcome-section">
        <h1>FutureForgeGear</h1>
        <p>Componenti e computer di alta qualità per ogni esigenza</p>
    </div>

    <div class="categories">
        <div class="category-card">
            <a href="product?categoria=FISSI">
                <img src="image/pcfisso1.jpg" alt="PC Fissi">
                <h3>PC Fissi</h3>
                <p>Desktop ad alte prestazioni</p>
            </a>
        </div>

        <div class="category-card">
            <a href="product?categoria=PORTATILI">
                <img src="image/pcportatile1.jpg" alt="PC Portatili">
                <h3>PC Portatili</h3>
                <p>Notebook per ogni necessità</p>
            </a>
        </div>

        <div class="category-card">
            <a href="product?categoria=COMPONENTI">
                <img src="image/periferiche1.jpg" alt="Componenti">
                <h3>Componenti</h3>
                <p>Schede, processori, RAM</p>
            </a>
        </div>

        <div class="category-card">
            <a href="tuttiProdotti.jsp">
                <img src="image/tutti-prodotti.jpg" alt="Tutti i prodotti">
                <h3>Tutti i Prodotti</h3>
                <p>Scopri il catalogo completo</p>
            </a>
        </div>
    </div>

    <div class="product-showcase">
        <h2>Perché scegliere noi</h2>
        <div class="features">
            <div class="feature">
                <h4>🛠️ Qualità</h4>
                <p>Componenti selezionati</p>
            </div>
            <div class="feature">
                <h4>⚡ Performance</h4>
                <p>Prestazioni garantite</p>
            </div>
            <div class="feature">
                <h4>🛡️ Garanzia</h4>
                <p>Assistenza dedicata</p>
            </div>
        </div>
    </div>
</div>


</body>
</html>