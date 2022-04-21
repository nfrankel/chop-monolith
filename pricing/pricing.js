module.exports = async function (context, req) {
    context.log('Pricing computed from the function')
    const lines = req.body.lines
    context.log(`Received cart lines: ${JSON.stringify(lines)}`)
    const price = lines.reduce(
        (current, line) => { return current + line.first.price * line.second },
        0.0
    )
    context.log(`Computed price: ${price}`)
    context.res = {
        body: { "price": price, "origin": "azure" }
    }
    context.done()
}
