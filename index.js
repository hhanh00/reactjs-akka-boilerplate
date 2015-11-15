import React, {Component, PropTypes} from 'react'
import ReactDOM from 'react-dom'

import { Router, Route, IndexRoute, Link, IndexLink } from 'react-router'
import { createHistory } from 'history'

import { observeProps, createEventHandler } from 'rx-recompose';
import { Observable } from 'rx';
import { get } from 'jquery';

const history = createHistory()

const Exposure = (props) => {
  return <div>
    <h2>Exposure - Team {props.team}</h2>
    Data: 
    <ul>
    {props.data.map(d => <li key={d.strategy}>{d.team}/{d.strategy}</li>)}
    </ul>
    <Link to="/exposure?team=A">Team A</Link>
    <Link to="/exposure?team=B">Team B</Link>
    <Link to="/exposure?team=C">Team C</Link>
  </div>
}

const ExposureContainer = observeProps(props$ => {
  const team$ = props$.map(p => p.location.query.team).distinctUntilChanged()
  const fetchData$ = team$.flatMap(team => {
    let promise = get(`http://localhost:3000/data?team=${team}`).promise()
    return Observable.fromPromise(promise)
  }).startWith([])
  return Observable.combineLatest(props$, team$, fetchData$, (props, team, data) => ({team: team, data: data, ...props}))
  }, Exposure)

const PnL = (props) => {
  return <div>
    <h2>PnL</h2>
  </div>
}

const App = (props) => {
  return <div>
    <h1>Links</h1>
    <ul>
      <li><Link to="/exposure">Exposure</Link></li>
      <li><Link to="/pnl">P & L</Link></li>
    </ul>
    {props.children}
  </div>
}

const routes = (
  <Route path="/" component={App}>
    <Route path="exposure" component={ExposureContainer} />
    <Route path="pnl" component={PnL} />
  </Route>
)

ReactDOM.render(
  <Router history={history}>
    {routes}
  </Router>
  , 
  document.getElementById('root'))
