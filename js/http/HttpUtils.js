import ContentType from './ContentType';
import HttpMethod from './HttpMethod';

export default class HttpUtils {

    static get(url) {
        return new Promise((resolve, reject) => {


            let headers = {
                'Content-Type': 'application/json',
            };
            fetch(url,{
                method: "GET",
                headers: headers,
                body: null
            })
                // .then(response => response.json())
                .then(result => {
                    console.log("HttpGet:" + result);
                    if(result.status===200){
                        resolve(result._bodyText);
                    }else {
                        reject(result);
                    }
                })
                .catch(error => {
                    console.log("HttpGetFail:" + error);
                    reject(error);
                })
        });
    }

    static post(url, data) {
        return new Promise((resolve, reject) => {
            fetch(url, {
                method: HttpMethod.POST,
                headers: {
                    'Accept': ContentType.JSON,
                    'Content-Type': ContentType.JSON,
                },
                body: JSON.stringify(data),
            })
                // .then(response => response.json())
                .then(result => {
                    resolve(result);
                })
                .catch(error => {
                    reject(error);
                })
        })
    }


}