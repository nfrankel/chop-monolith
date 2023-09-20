import {backend} from './env.js'

export const fetchVersion = async event => {
        const response = await fetch(`${backend()}/version`)
        displayVersion(await response.json())
    }

const displayVersion = async version => {
    let versionSpan = document.getElementById('version')
    versionSpan.innerHTML = version.value
}
