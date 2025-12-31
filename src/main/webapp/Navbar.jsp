<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<style>
  .navbar {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    background-color: #2c3e50;
    padding: 15px 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    z-index: 1000;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  }

  .logo {
    color: white;
    font-size: 24px;
    font-weight: bold;
    text-decoration: none;
  }

  .logo:hover {
    color: #3498db;
  }

  .nav-links {
    display: flex;
    list-style: none;
    gap: 20px;
  }

  .nav-links a {
    color: white;
    text-decoration: none;
    padding: 8px 16px;
    border-radius: 4px;
    transition: background-color 0.3s;
  }

  .nav-links a:hover {
    background-color: #3498db;
  }

  .search-form {
    display: flex;
    gap: 10px;
  }

  .search-input {
    padding: 8px 12px;
    border: none;
    border-radius: 4px;
    width: 200px;
  }

  .search-button {
    padding: 8px 16px;
    background-color: #3498db;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }

  .search-button:hover {
    background-color: #2980b9;
  }

  .user-actions {
    display: flex;
    gap: 15px;
    align-items: center;
  }

  .user-actions a {
    color: white;
    text-decoration: none;
    padding: 8px 12px;
    border-radius: 4px;
    background-color: #34495e;
  }

  .user-actions a:hover {
    background-color: #3498db;
  }

  .hamburger {
    display: none;
    flex-direction: column;
    cursor: pointer;
    gap: 4px;
  }

  .hamburger span {
    width: 25px;
    height: 3px;
    background-color: white;
  }

  @media (max-width: 768px) {
    .hamburger {
      display: flex;
    }

    .nav-links {
      display: none;
      position: absolute;
      top: 100%;
      left: 0;
      right: 0;
      background-color: #2c3e50;
      flex-direction: column;
      padding: 20px;
    }

    .nav-links.active {
      display: flex;
    }

    .search-form {
      display: none;
    }
  }

  body {
    padding-top: 70px;
  }
</style>

<div class="navbar">
  <a href="home.jsp" class="logo">FutureForgeGear</a>

  <div class="hamburger" id="hamburger">
    <span></span>
    <span></span>
    <span></span>
  </div>

  <ul class="nav-links" id="navLinks">
    <li><a href="home.jsp">Home</a></li>
    <li><a href="tuttiProdotti.jsp">Prodotti</a></li>
    <li><a href="chi-siamo.jsp">Chi Siamo</a></li>
    <li><a href="contatti.jsp">Contatti</a></li>
  </ul>

  <form class="search-form" action="product" method="GET">
    <input type="hidden" name="action" value="search">
    <input type="text" name="nome" class="search-input" placeholder="Cerca prodotti...">
    <button type="submit" class="search-button">Cerca</button>
  </form>

  <div class="user-actions">
    <% if (session.getAttribute("Email") == null) { %>
    <a href="login.jsp">Login</a>
    <% } else { %>
    <a href="logout">Logout</a>
    <a href="profilo.jsp">Profilo</a>
    <% } %>
    <a href="carrello.jsp">Carrello</a>
  </div>
</div>

<script>
  $(document).ready(function() {
    $('#hamburger').click(function() {
      $('#navLinks').toggleClass('active');
    });
  });
</script>