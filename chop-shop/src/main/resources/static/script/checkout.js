(function () {
    const removeFromCheckout = async event => {
        const response = await fetch(
            `/checkout/remove/${event.target.dataset.productId}`,
            { method: 'DELETE' }
        )
        location.assign(await response.headers.get('location'))
    }
    const attachEventHandlerToRemoveButton = button => {
        button.onclick = removeFromCheckout
    }
    const attachEventHandlers = () => {
        document.querySelectorAll('.remove-from-checkout').forEach(attachEventHandlerToRemoveButton)
    }
    window.onload = () => {
        attachEventHandlers()
    }
})()