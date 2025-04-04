import { useState, useMemo, useEffect } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import { Stomp } from '@stomp/stompjs';
import './App.css'

function App() {
  const [loadMessage, setLoadMessages] = useState(false);
  const [loader, setLoader] = useState(false);
  const [messages, setMessages] = useState([]);
  const client = useMemo(()=>{
    return Stomp.client("ws://localhost:8080/ws")
  }, []);

  useEffect(()=>{
    setLoader(true);
    client.connect({}, ()=>{
      client.subscribe('/getMessages', (e)=>{
        console.log('Recieve Message', e.body)
        setMessages(JSON.parse(e.body))
      });
      client.subscribe('/updateMessages', (e)=>{
        console.log('Update Message', e.body)
      });
    })
  }, [client, loadMessage, setMessages, setLoader]);

  return (
    <>
      <div>
        <a href="https://vite.dev" target="_blank">
          <img src={viteLogo} className="logo" alt="Vite logo" />
        </a>
        <a href="https://react.dev" target="_blank">
          <img src={reactLogo} className="logo react" alt="React logo" />
        </a>
      </div>
      <h1>Vite + React</h1>
      <div className="card">
        <button onClick={() => client.send('/requestMessages', {}, "{}")}>
        </button>
        <ul>
          {messages.map((message, index)=>(<li key={`${message._id.timestamp}-${index}`}>
            <p>{message.sender}</p>
            <p>{message.dest}</p>
            <p>{message.content}</p>
          </li>))}
        </ul>
      </div>
    </>
  )
}

export default App
