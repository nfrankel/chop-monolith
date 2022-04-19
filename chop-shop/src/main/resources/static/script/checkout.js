(function () {
    const fetchCheckout = async () => {
        const response = await fetch('/checkout/c')
        displayCheckout(await response.json())
        attachEventHandlers()
    }
    const displayCheckout = checkout => {
        const tbody = document.getElementById('checkout')
        while (tbody.firstChild) {
            tbody.removeChild(tbody.firstChild);
        }
        checkout.lines.forEach(displayCheckoutLine)
        let total = new Intl.NumberFormat('en', { style: 'currency', currency: 'USD' }).format(checkout.total)
        document.getElementById('total').innerHTML = total
    }
    const displayCheckoutLine = line => {
        const fragment = document.querySelector('#line').content.cloneNode(true)
        const image = fragment.querySelector('img')
        image.src = `/image/${line.first.id}.jpg`
        const nameCell = fragment.querySelectorAll('td')[1]
        nameCell.innerHTML = line.first.name
        const priceCell = fragment.querySelectorAll('td')[2]
        let price = new Intl.NumberFormat('en', { style: 'currency', currency: 'USD' }).format(line.first.price)
        priceCell.innerHTML = price
        const qtyCell = fragment.querySelectorAll('td')[3]
        qtyCell.innerHTML = line.second
        const btn = fragment.querySelector('a')
        btn.dataset.productId = line.first.id
        const icon = fragment.querySelector('i')
        icon.dataset.productId = line.first.id
        document.getElementById('checkout').appendChild(fragment)
    }
    const removeFromCheckout = async event => {
        const response = await fetch(
            `/checkout/remove/${event.target.dataset.productId}`,
            { method: 'DELETE' }
        )
        displayCheckout(await response.json())
    }
    const attachEventHandlerToRemoveButton = button => {
        button.onclick = removeFromCheckout
    }
    const attachEventHandlers = () => {
        document.querySelectorAll('.remove-from-checkout').forEach(attachEventHandlerToRemoveButton)
    }
    window.onload = () => {
        fetchCheckout()
    }
})()