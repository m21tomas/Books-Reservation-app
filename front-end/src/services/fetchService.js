function ajax(url, requestMethod, jwt, requestBody) {
  const fetchData = {
    headers: {
      "Content-Type": "application/json",
    },
    method: requestMethod,
  };

  if (jwt) {
    fetchData.headers.Authorization = `Bearer ${jwt}`;
    //fetchData.headers.Authorization = `jwt=${jwt}`;
  }

  if (requestBody) {
    fetchData.body = JSON.stringify(requestBody);
  }

  return fetch(url, fetchData)
    .then((response) => {
      if (response.status === 200) {
        const contentType = response.headers.get("content-type");
        if (contentType && contentType.indexOf("application/json") !== -1) {
          return response.json();
        } else {
          return response.text();
        }
      }
    })
    .catch((error) => error.json());
}

export default ajax;
