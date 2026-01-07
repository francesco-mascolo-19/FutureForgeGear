<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="model.OrderManagement.Prodotto" %>
<%@ page import="java.util.List" %>

<%
    List<Prodotto> prodotti = (List<Prodotto>) request.getAttribute("prodotti");
    if (prodotti == null) {
        System.out.println("Prodotti is null in JSP");
    } else {
        System.out.println("Number of products in JSP: " + prodotti.size());
    }
%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Tutti i Prodotti - FutureForgeGear</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 0;
            background-color: #f5f5f5;
        }

        .content {
            max-width: 1200px;
            margin: 100px auto 50px;
            padding: 20px;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        h1 {
            color: #2c3e50;
            text-align: center;
            margin-bottom: 30px;
        }

        #product-list {
            list-style: none;
            padding: 0;
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
            gap: 20px;
        }

        #product-list li {
            background-color: white;
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 15px;
            transition: transform 0.3s, box-shadow 0.3s;
        }

        #product-list li:hover {
            transform: translateY(-5px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
            border-color: #3498db;
        }

        #product-list a {
            text-decoration: none;
            color: #2c3e50;
            display: block;
        }

        .product-name {
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 10px;
            color: #3498db;
        }

        .product-price {
            color: #27ae60;
            font-size: 16px;
            font-weight: bold;
        }

        .product-category {
            color: #7f8c8d;
            font-size: 14px;
            margin-top: 5px;
        }

        .no-products {
            text-align: center;
            padding: 40px;
            color: #7f8c8d;
            font-size: 18px;
        }
    </style>
</head>
<body>
<%@include file="Navbar.jsp" %>

<div class="content">
    <h1>Tutti i Prodotti</h1>

    <% if (prodotti != null && !prodotti.isEmpty()) { %>
    <ul id="product-list">
        <% for (Prodotto prodotto : prodotti) { %>
        <li>
            <a href="productDetails.jsp?id=<%= prodotto.getId() %>">
                <div class="product-name"><%= prodotto.getNome() %></div>
                <% if (prodotto.getDescrizione() != null && !prodotto.getDescrizione().isEmpty()) { %>
                <div class="product-description">
                    <%= prodotto.getDescrizione().length() > 100 ?
                            prodotto.getDescrizione().substring(0, 100) + "..." :
                            prodotto.getDescrizione() %>
                </div>
                <% } %>
                <div class="product-price">€ <%= String.format("%.2f", prodotto.getPrezzo()) %></div>
                <% if (prodotto.getCategoria() != null) { %>
                <div class="product-category">Categoria: <%= prodotto.getCategoria() %></div>
                <% } %>
            </a>
        </li>
        <% } %>
    </ul>
    <% } else { %>
    <div class="no-products">
        Nessun prodotto disponibile al momento.
    </div>
    <% } %>
</div>

<%@include file="footer.jsp" %>
</body>
</html>