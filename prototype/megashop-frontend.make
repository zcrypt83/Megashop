FIGMA_MAKE_PROJECT {
  metadata {
    name: "Megashop - E-Commerce Delivery Platform"
    language: "es-PE"
    target: "Responsive web application"
    framework_reference: "React + Bootstrap + Lucide icons"
    viewport_desktop: "1440x1024"
    viewport_tablet: "834x1194"
    viewport_mobile: "390x844"
  }

  prompt:
  """
  Diseña un prototipo funcional de alta fidelidad para Megashop, una plataforma
  peruana de comercio electrónico y delivery. Debe sentirse como una herramienta
  operativa moderna: clara, rápida, profesional y fácil de escanear.

  No crear una landing page. La primera pantalla debe ser el catálogo utilizable.
  Implementar navegación entre catálogo, seguimiento, dashboard y acceso.
  Usar componentes reutilizables, estados vacíos, loading, error y confirmación.
  """

  design_system {
    colors {
      ink: "#172026"
      muted: "#64717D"
      border: "#D8E1E8"
      canvas: "#EEF3F6"
      surface: "#FFFFFF"
      soft_surface: "#F3F6F8"
      primary: "#0F8B8D"
      primary_dark: "#0B6466"
      accent: "#F2B84B"
      success: "#16856B"
      danger: "#C84646"
    }

    typography {
      family: "Inter"
      page_title: "28px / 700"
      section_title: "18px / 700"
      card_title: "16px / 700"
      body: "14px / 400"
      small: "12px / 500"
      letter_spacing: "0"
    }

    geometry {
      radius: "8px"
      border: "1px solid #D8E1E8"
      sidebar_width: "260px"
      content_padding: "28px"
      grid_gap: "16px"
      button_height: "40px"
      input_height: "44px"
    }

    iconography {
      library: "Lucide"
      stroke: "1.8px"
      default_size: "18px"
    }
  }

  shared_components [
    {
      name: "AppSidebar"
      content: "Marca Megashop, Productos, Seguimiento, Admin, Acceso y contador del carrito"
      behavior: "Sticky en escritorio; navegación superior compacta en móvil"
    },
    {
      name: "Topbar"
      content: "Contexto MongoDB + Redis, título de la vista y botón de perfil"
    },
    {
      name: "SearchField"
      states: ["default", "focus", "filled", "disabled"]
    },
    {
      name: "ProductCard"
      content: "Imagen 16:10, categoría, nombre, descripción, precio, stock y botón Agregar"
      states: ["default", "hover", "low-stock", "out-of-stock"]
    },
    {
      name: "StatusBadge"
      variants: ["pendiente", "confirmado", "preparando", "en_ruta", "entregado", "cancelado"]
    },
    {
      name: "MetricCard"
      content: "Ícono, etiqueta, valor y variación"
    },
    {
      name: "Toast"
      variants: ["success", "error", "information"]
    }
  ]

  screens [
    {
      id: "catalog-desktop"
      name: "01 Catálogo y checkout"
      route: "/"
      layout:
      """
      Sidebar fija de 260px. Área principal con topbar. Debajo, grid de dos
      columnas: catálogo flexible y checkout de 340px. Barra de búsqueda y filtro
      de categoría arriba. Mostrar seis tarjetas de productos reales.
      """
      interactions: [
        "Buscar filtra productos mientras se escribe",
        "Categoría actualiza el grid",
        "Agregar incrementa el carrito y muestra toast",
        "Más/menos actualiza cantidad",
        "Eliminar retira el producto",
        "Confirmar pedido abre modal de dirección y pago"
      ]
    },
    {
      id: "product-detail"
      name: "02 Detalle de producto"
      route: "/productos/:id"
      layout:
      """
      Imagen principal grande sin decoración excesiva. A la derecha: categoría,
      nombre, precio, disponibilidad, selector de cantidad, descripción, etiquetas
      y acción Agregar al carrito. Debajo, productos relacionados.
      """
    },
    {
      id: "checkout"
      name: "03 Confirmación de pedido"
      route: "/checkout"
      layout:
      """
      Flujo de tres pasos: dirección, pago y confirmación. Mostrar resumen del
      carrito siempre visible en escritorio. Métodos: tarjeta, Yape, Plin y
      efectivo. Usar stepper horizontal y validación inline.
      """
    },
    {
      id: "tracking"
      name: "04 Seguimiento"
      route: "/tracking"
      layout:
      """
      Mapa operativo a la izquierda con ruta y marcador de repartidor. Panel a la
      derecha con número de pedido, ETA, repartidor y timeline: confirmado,
      preparando, en ruta, entregado.
      """
      states: ["sin_pedido", "preparando", "en_ruta", "entregado", "cancelado"]
    },
    {
      id: "login"
      name: "05 Acceso"
      route: "/login"
      layout:
      """
      Formulario centrado de máximo 420px: correo, contraseña, recordar sesión,
      ingresar, recuperar contraseña y enlace de registro. Mantener el fondo
      operativo de la aplicación sin hero comercial.
      """
    },
    {
      id: "admin-dashboard"
      name: "06 Dashboard administrativo"
      route: "/dashboard"
      layout:
      """
      Cuatro métricas: clientes, productos, pedidos y ventas. Segunda fila con
      gráfico de ventas por canal y cola de entregas. Tercera fila con tabla de
      pedidos recientes y ranking de productos más vistos.
      """
      interactions: [
        "Filtro de periodo: hoy, 7 días, 30 días",
        "Click en estado filtra pedidos",
        "Click en pedido abre detalle lateral",
        "Exportar genera reporte"
      ]
    },
    {
      id: "admin-products"
      name: "07 Gestión de productos"
      route: "/dashboard/productos"
      layout:
      """
      Tabla densa con imagen, SKU, nombre, categoría, precio, stock, estado y menú
      de acciones. Toolbar con búsqueda, filtros, crear producto e importar.
      Modal lateral para crear y editar.
      """
    },
    {
      id: "mobile-catalog"
      name: "08 Catálogo móvil"
      route: "/"
      viewport: "390x844"
      layout:
      """
      Header compacto con marca, búsqueda y carrito. Navegación inferior con
      Productos, Pedidos, Seguimiento y Perfil. Una tarjeta por fila. Checkout
      abre como bottom sheet.
      """
    }
  ]

  prototype_links [
    "Productos -> Detalle de producto"
    "Agregar -> Checkout"
    "Confirmar pedido -> Seguimiento"
    "Admin -> Dashboard administrativo"
    "Productos del dashboard -> Gestión de productos"
    "Acceso exitoso -> Catálogo o Dashboard según rol"
  ]

  accessibility {
    contrast: "WCAG AA"
    focus: "Indicador visible de 2px"
    touch_targets: "Mínimo 44x44px en móvil"
    forms: "Labels persistentes, mensajes asociados y navegación por teclado"
    images: "Texto alternativo descriptivo"
  }

  acceptance_criteria [
    "Todas las pantallas enlazadas y navegables"
    "Componentes convertidos en variantes reutilizables"
    "Responsive en 1440px, 834px y 390px"
    "Sin texto cortado ni elementos superpuestos"
    "Estados loading, vacío, error y éxito visibles"
    "Paleta y espaciado consistentes con el sistema definido"
  ]
}
