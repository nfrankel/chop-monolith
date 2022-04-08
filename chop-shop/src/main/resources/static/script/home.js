(function () {
    const fetchCatalog = async event => {
        const response = await fetch('/catalog')
        displayProducts(await response.json())
        attachEventHandlers()
        displayCart()
    }
    const displayProducts = products => {
        products.forEach(displayProduct)
    }
    const displayProduct = product => {
        const fragment = document.querySelector('#product').content.cloneNode(true)
        const card = fragment.querySelector('.card')
        if (!product.hero) {
            const ribbon = card.querySelector('.ribbon')
            ribbon.remove()
        }
        const img = card.querySelector('img')
        img.src = `/image/${product.id}.jpg`
        const title = card.querySelector('h5')
        title.innerText = product.name
        const text = card.querySelector('p')
        text.innerText = product.description
        const a = card.querySelector('a.btn')
        a.dataset.productId = product.id
        const icon = card.querySelector('i.bi')
        icon.dataset.productId = product.id
        const badge = card.querySelector('span.badge')
        badge.innerText = new Intl.NumberFormat('en', { style: 'currency', currency: 'USD' }).format(product.price)
        badge.dataset.productId = product.id
        document.getElementById('products').appendChild(fragment)
    }
    const displayCart = async event => {
        const response = await fetch('/cart/quantity')
        updateQuantity(await response.json())
    }
    const addToCart = async event => {
        const response = await fetch(
            `/cart/add/${event.target.dataset.productId}`,
            { method: 'POST' }
        )
        updateQuantity(await response.json())
    }
    const updateQuantity = async json => {
        let itemCount = document.getElementById('itemCount')
        itemCount.innerHTML = json.quantity
        let cartIcon = document.getElementById('cartIcon')
        cartIcon.classList.add('bi-cart-fill')
        cartIcon.classList.remove('bi-cart')
    }
    const attachEventHandlerToProductButton = button => {
        button.onclick = addToCart
    }
    const attachEventHandlers = () => {
        document.querySelectorAll('.add-to-cart').forEach(attachEventHandlerToProductButton)
    }
    window.onload = () => {
        fetchCatalog()
    }
})()