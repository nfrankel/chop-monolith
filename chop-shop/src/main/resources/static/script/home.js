(function () {
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
        attachEventHandlers()
    }
})()